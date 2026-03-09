@import com.example.septic.web.PageMeta
@param PageMeta page
@param String message

@template.layouts.app(page = page, content = @`
    <section class="section section--heroish">
        <div class="container narrow">
            <span class="eyebrow">404</span>
            <h1>Page not found</h1>
            <p class="lede">${message}</p>
            <a class="button button--primary" href="/">Go home</a>
        </div>
    </section>
`)
