import unittest

from tools.check_source_health import (
    ACTIONABLE_CLASSIFICATIONS,
    classify,
    classify_network_error,
    nonproduction_host_marker,
    should_retry,
)
from unittest.mock import patch


class SourceHealthPolicyTests(unittest.TestCase):
    def test_http_classification_keeps_confirmed_missing_urls_dead(self):
        self.assertEqual("dead", classify(404))
        self.assertEqual("dead", classify(410))
        self.assertEqual("blocked", classify(403))
        self.assertEqual("blocked", classify(521))
        self.assertEqual("transient", classify(503))

    def test_dns_failure_is_actionable(self):
        classification = classify_network_error(
            "URLError: <urlopen error [Errno -2] Name or service not known>"
        )
        self.assertEqual("dns_error", classification)
        self.assertIn(classification, ACTIONABLE_CLASSIFICATIONS)

    def test_hostname_certificate_mismatch_is_actionable(self):
        classification = classify_network_error(
            "certificate verify failed: Hostname mismatch, certificate is not valid for 'temp.example.gov'"
        )
        self.assertEqual("certificate_hostname_error", classification)
        self.assertIn(classification, ACTIONABLE_CLASSIFICATIONS)

    def test_incomplete_certificate_chain_stays_manual_review(self):
        classification = classify_network_error(
            "SSL: CERTIFICATE_VERIFY_FAILED unable to get local issuer certificate"
        )
        self.assertEqual("tls_error", classification)
        self.assertNotIn(classification, ACTIONABLE_CLASSIFICATIONS)

    def test_nonproduction_hostname_markers_are_detected_without_flagging_codev(self):
        self.assertEqual("old", nonproduction_host_marker("https://old.example.gov/path"))
        self.assertEqual("temp", nonproduction_host_marker("https://temp.example.gov/path"))
        self.assertEqual("uat", nonproduction_host_marker("https://prmduat.example.gov/path"))
        self.assertEqual("", nonproduction_host_marker("https://codev.utahcounty.gov/path"))

    def test_retry_policy_targets_network_and_server_failures(self):
        self.assertTrue(should_retry({"statusCode": 503, "classification": "transient"}))
        self.assertTrue(should_retry({"statusCode": 0, "classification": "dns_error"}))
        self.assertFalse(should_retry({"statusCode": 403, "classification": "blocked"}))
        self.assertFalse(should_retry({"statusCode": 200, "classification": "healthy"}))

    @patch("tools.check_source_health.audit_url_once")
    def test_server_error_becomes_actionable_only_after_retries(self, audit_once):
        from tools.check_source_health import audit_url

        audit_once.return_value = {
            "url": "https://example.gov/form.pdf",
            "statusCode": 503,
            "classification": "transient",
            "error": "HTTP Error 503",
        }
        result = audit_url(
            {"url": "https://example.gov/form.pdf", "sourceIds": ["x"], "agencies": ["Example"]},
            timeout=5,
            retries=2,
            retry_delay=0,
        )
        self.assertEqual(3, audit_once.call_count)
        self.assertEqual("persistent_server_error", result["classification"])
        self.assertIn(result["classification"], ACTIONABLE_CLASSIFICATIONS)

    @patch("tools.check_source_health.audit_url_once")
    def test_edge_gateway_status_stays_manual_review_after_retry(self, audit_once):
        from tools.check_source_health import audit_url

        audit_once.return_value = {
            "url": "https://example.gov/form.pdf",
            "statusCode": 521,
            "classification": "blocked",
            "error": "HTTP Error 521",
        }
        result = audit_url(
            {"url": "https://example.gov/form.pdf", "sourceIds": ["x"], "agencies": ["Example"]},
            timeout=5,
            retries=2,
            retry_delay=0,
        )
        self.assertEqual(3, audit_once.call_count)
        self.assertEqual("blocked", result["classification"])
        self.assertNotIn(result["classification"], ACTIONABLE_CLASSIFICATIONS)

    @patch("tools.check_source_health.urllib.request.urlopen")
    def test_public_dns_confirmation_requires_authoritative_nxdomain(self, urlopen):
        from tools.check_source_health import confirm_dns_nxdomain

        response = urlopen.return_value.__enter__.return_value
        response.read.return_value = b'{"Status": 3}'
        self.assertTrue(confirm_dns_nxdomain("https://missing.example.gov/path", 5))

        response.read.return_value = b'{"Status": 0,"Answer":[{"data":"192.0.2.1"}]}'
        self.assertFalse(confirm_dns_nxdomain("https://resolves.example.gov/path", 5))


if __name__ == "__main__":
    unittest.main()
