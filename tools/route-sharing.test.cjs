const test = require('node:test');
const assert = require('node:assert/strict');
const { buildPublicUrl, install } = require('../src/main/resources/static/route-sharing.js');

const origin = 'https://septicpath.com';
const county = '/septic-records-checklist/michigan/livingston-county/';
const index = '/septic-records-access-index/';
const storageKey = 'septicpath_public_route_share_v1';

function browser(options = {}) {
    const stored = options.stored || new Map();
    const events = [], ga = [], copies = [], native = [], listeners = [], timers = [];
    let removed = 0;
    const doc = {
        referrer: options.referrer || 'https://example.org/resources/?address=PRIVATE#CUSTOMER',
        body: { appendChild() {} },
        createElement() {
            return { style: {}, setAttribute() {}, select() {}, remove() { removed += 1; } };
        },
        execCommand() { return options.legacyCopy ?? false; },
        addEventListener(type, listener) { if (type === 'click') listeners.push(listener); }
    };
    const win = {
        document: doc,
        location: new URL(options.url || origin + county),
        isSecureContext: true,
        navigator: {
            sendBeacon(_endpoint, blob) { events.push(JSON.parse(blob.text)); return true; },
            clipboard: { async writeText(value) {
                copies.push(value);
                if (options.clipboardReject) throw new Error('Denied');
            } }
        },
        Blob: class { constructor(parts) { this.text = parts.join(''); } },
        fetch() { throw new Error('Unexpected fetch'); },
        sessionStorage: {
            getItem(key) {
                if (options.storageDenied) throw new Error('Blocked');
                return stored.get(key) ?? null;
            },
            setItem(key, value) {
                if (options.storageDenied || options.storageWriteDenied) throw new Error('Blocked');
                stored.set(key, value);
            }
        },
        matchMedia() { return { matches: options.mobile ?? false }; },
        setTimeout(fn) { timers.push(fn); },
        gtag(...args) {
            if (options.gtagThrows) throw new Error('Analytics unavailable');
            ga.push(args);
        }
    };
    if (options.nativeShare) win.navigator.share = async (payload) => {
        native.push(payload);
        if (options.nativeAbort) throw Object.assign(new Error('Cancelled'), { name: 'AbortError' });
    };
    function clickOfficial(href = 'https://county.example/records/?parcel=PRIVATE', attribute = null) {
        const link = { href, getAttribute() { return attribute; } };
        const target = { closest(selector) { return selector === '[data-share-route]' ? null : link; } };
        listeners.forEach(listener => listener({ target }));
    }
    return { win, events, ga, copies, native, stored, clickOfficial, removed: () => removed };
}

function button(url = origin + county) {
    return { dataset: { shareUrl: url, shareTitle: 'Livingston County records' }, textContent: 'Share county guide' };
}
function actions(environment) { return environment.events.map(event => event.action); }

test('real county route shape is shareable and strips property/query/fragment data', () => {
    const result = buildPublicUrl(county + '?address=PRIVATE&src=CUSTOMER&utm_content=PRIVATE#PRIVATE', origin);
    assert.equal(result, origin + county + '?src=shared-route');
});

test('public index preserves enumerated filters but never free-text search or other query values', () => {
    const result = new URL(buildPublicUrl(index + '?state=NC&method=online&artifact=as-built&confidence=high&parcelOnly=true&q=PRIVATE&address=PRIVATE&workflowRunId=PRIVATE#PRIVATE', origin));
    assert.deepEqual(Object.fromEntries(result.searchParams), {
        state: 'NC', method: 'online', artifact: 'as-built', confidence: 'high', parcelOnly: 'true', src: 'shared-route'
    });
    assert.equal(result.hash, '#records-access-county-search');
    assert.equal(result.href.includes('PRIVATE'), false);
});

test('invalid filter values and duplicate injected values do not escape into share link', () => {
    const result = new URL(buildPublicUrl(index + '?state=PRIVATE&method=PRIVATE&artifact=PRIVATE&confidence=PRIVATE&parcelOnly=PRIVATE&state=NC', origin));
    assert.deepEqual(Object.fromEntries(result.searchParams), { src: 'shared-route' });
});

test('external URLs, executable schemes and private/non-route paths are rejected', () => {
    for (const raw of ['https://evil.example' + county, '//evil.example' + county, 'javascript:alert(1)', '/unlock/PRIVATE/', '/paid-unlock/PRIVATE/', '/septic-record-finder/?address=PRIVATE']) {
        assert.equal(buildPublicUrl(raw, origin), null, raw);
    }
});

test('clipboard success is distinct from intent; copies only sanitized public URL', async () => {
    const env = browser();
    await install(env.win).share(button(origin + county + '?address=PRIVATE'));
    assert.deepEqual(actions(env), ['share_clicked', 'share_copied']);
    assert.deepEqual(env.copies, [origin + county + '?src=shared-route']);
    assert.equal(JSON.stringify(env.events).includes('PRIVATE'), false);
});

test('sharing attributes the public source surface separately from destination and rejects private source paths', async () => {
    for (const nativeShare of [false, true]) {
        for (const sourcePath of ['/septic-record-finder/', county, index, '/unlock/PRIVATE-TOKEN/', '/unknown/PRIVATE-TOKEN/']) {
            const env = browser({ url: origin + sourcePath + '?address=PRIVATE#PRIVATE', nativeShare, mobile: nativeShare });
            await install(env.win).share(button());
            const expectedSource = sourcePath.includes('PRIVATE-TOKEN') ? county : sourcePath;
            assert.deepEqual(actions(env), ['share_clicked', nativeShare ? 'share_handoff' : 'share_copied']);
            for (const event of env.events) assert.equal(event.sourcePage, expectedSource);
            for (const [, , params] of env.ga) {
                assert.equal(params.page_location, origin + expectedSource);
                assert.equal(params.route_path, county);
                assert.equal(params.measurement_version, 'public_share_v2');
            }
            assert.equal(JSON.stringify([env.events, env.ga]).includes('PRIVATE'), false);
        }
    }
});

test('clipboard rejection and failed legacy fallback do not claim successful copying', async () => {
    const env = browser({ clipboardReject: true });
    const control = button();
    await install(env.win).share(control);
    assert.deepEqual(actions(env), ['share_clicked']);
    assert.equal(control.textContent, 'Copy unavailable');
    assert.equal(control.dataset.shareBusy, 'false');
    assert.equal(env.removed(), 1);
});

test('legacy clipboard fallback reports copied only after successful command', async () => {
    const env = browser({ clipboardReject: true, legacyCopy: true });
    await install(env.win).share(button());
    assert.deepEqual(actions(env), ['share_clicked', 'share_copied']);
    assert.equal(env.removed(), 1);
});

test('native share cancellation records intent only and does not silently copy', async () => {
    const env = browser({ mobile: true, nativeShare: true, nativeAbort: true });
    const control = button();
    await install(env.win).share(control);
    assert.deepEqual(actions(env), ['share_clicked']);
    assert.equal(env.copies.length, 0);
    assert.equal(control.dataset.shareBusy, 'false');
});

test('native resolved handoff is a separate action from copying', async () => {
    const env = browser({ mobile: true, nativeShare: true });
    await install(env.win).share(button());
    assert.deepEqual(actions(env), ['share_clicked', 'share_handoff']);
    assert.equal(env.copies.length, 0);
    assert.equal(env.native[0].url, origin + county + '?src=shared-route');
});

test('invalid incoming marker or non-public page cannot create a shared-link arrival', () => {
    for (const url of [origin + county + '?src=PRIVATE', origin + county, origin + '/unlock/PRIVATE/?src=shared-route']) {
        const env = browser({ url });
        install(env.win);
        env.clickOfficial();
        assert.deepEqual(actions(env), [], url);
    }
});

test('one tagged arrival and one first external HTTPS official action per bounded tab session', () => {
    const env = browser({ url: origin + county + '?src=shared-route&address=PRIVATE' });
    install(env.win);
    env.clickOfficial('mailto:office@example.org');
    env.clickOfficial('http://county.example/records/');
    env.clickOfficial(origin + '/contact/');
    env.clickOfficial('https://county.example/records/', 'false');
    assert.deepEqual(actions(env), ['share_arrival']);
    env.clickOfficial();
    env.clickOfficial();
    assert.deepEqual(actions(env), ['share_arrival', 'share_official_open']);
    const reload = browser({ url: env.win.location.href, stored: env.stored });
    install(reload.win);
    reload.clickOfficial();
    assert.deepEqual(actions(reload), []);
    assert.equal(JSON.stringify(env.events).includes('PRIVATE'), false);
});

test('attribution continues to a subsequent public page without requiring marker propagation', () => {
    const first = browser({ url: origin + index + '?state=NC&src=shared-route' });
    install(first.win);
    const next = browser({ stored: first.stored });
    install(next.win);
    next.clickOfficial();
    assert.deepEqual(actions(next), ['share_official_open']);
    assert.equal(next.events[0].sourcePage, index);
    assert.equal(next.events[0].artifactType, 'public_route_index');
});

test('expired visit is not attributed and a newly tagged arrival starts a fresh visit', () => {
    const stored = new Map([[storageKey, JSON.stringify({ landing: county, expires: Date.now() - 1, officialOpened: false })]]);
    const old = browser({ stored });
    install(old.win);
    old.clickOfficial();
    assert.deepEqual(actions(old), []);
    const fresh = browser({ stored, url: origin + county + '?src=shared-route' });
    install(fresh.win);
    assert.deepEqual(actions(fresh), ['share_arrival']);
});

test('fully blocked session storage retains in-memory deduplication and does not block sharing', async () => {
    const env = browser({ storageDenied: true, url: origin + county + '?src=shared-route' });
    const api = install(env.win);
    env.clickOfficial();
    env.clickOfficial();
    await api.share(button());
    assert.deepEqual(actions(env), ['share_arrival', 'share_official_open', 'share_clicked', 'share_copied']);
});

test('readable but unwritable session storage also retains in-memory first-action attribution', () => {
    const env = browser({ storageWriteDenied: true, url: origin + county + '?src=shared-route' });
    install(env.win);
    env.clickOfficial();
    env.clickOfficial();
    assert.deepEqual(actions(env), ['share_arrival', 'share_official_open']);
});

test('GA event location/referrer and categorical payload omit property queries and fragments', () => {
    const env = browser({ url: origin + county + '?src=shared-route&address=PRIVATE#CUSTOMER' });
    install(env.win);
    assert.equal(env.ga.length, 1);
    const params = env.ga[0][2];
    assert.equal(params.page_location, origin + county);
    assert.equal(params.page_referrer, 'https://example.org');
    assert.equal(JSON.stringify(env.ga).includes('PRIVATE'), false);
    assert.equal(JSON.stringify(env.ga).includes('CUSTOMER'), false);
});

test('private result identifiers in referrer paths never enter GA sharing events', () => {
    for (const privatePath of ['/unlock/PRIVATE-TOKEN/', '/paid-unlock/PRIVATE-TOKEN/']) {
        const env = browser({ url: origin + county + '?src=shared-route', referrer: origin + privatePath });
        install(env.win);
        assert.equal(env.ga[0][2].page_referrer, origin);
        assert.equal(JSON.stringify(env.ga).includes('PRIVATE-TOKEN'), false);
    }
});

test('follow-on official action uses public landing path instead of a private current-page path', () => {
    const first = browser({ url: origin + county + '?src=shared-route' });
    install(first.win);
    const next = browser({ stored: first.stored, url: origin + '/unlock/PRIVATE-TOKEN/' });
    install(next.win);
    next.clickOfficial();
    assert.deepEqual(actions(next), ['share_official_open']);
    assert.equal(next.ga[0][2].page_location, origin + county);
    assert.equal(JSON.stringify(next.ga).includes('PRIVATE-TOKEN'), false);
});

test('analytics exceptions cannot block sharing or leave the button busy', async () => {
    const env = browser({ gtagThrows: true });
    const control = button();
    await install(env.win).share(control);
    assert.deepEqual(actions(env), ['share_clicked', 'share_copied']);
    assert.equal(control.dataset.shareBusy, 'false');
});
