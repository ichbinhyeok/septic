# Private checkout and delivery

Preview routes:

- `/design-preview/studio/checkout/`
- `/design-preview/studio/delivery/`

The checkout puts the reviewed evidence and its limits before the price. The private delivery view makes the original records, reviewed findings, expiry and remaining download allowance visible before the file is consumed.

Production behavior remains capability-token based and `noindex,nofollow,noarchive`. The payment capture response retains the direct `downloadUrl` for compatibility and adds `deliveryUrl`; the browser and delivery email now open the private delivery view. Viewing the delivery page does not consume a download. The package hash, payment amount, release approval, expiry and maximum-download checks remain enforced by `PaidUnlockStore`.

Mockups:

- `mockups/checkout-v1.webp`
- `mockups/delivery-v1.webp`
