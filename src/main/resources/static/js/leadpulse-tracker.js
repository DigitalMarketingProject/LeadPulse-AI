(function () {
    "use strict";

    const script = document.currentScript;
    const endpoint = script && script.dataset.endpoint;
    const apiKey = script && script.dataset.apiKey;
    if (!endpoint || !apiKey) {
        return;
    }

    const consentKey = "leadpulse_tracking_consent";
    const visitorKey = "leadpulse_visitor_id";
    const sessionKey = "leadpulse_session_id";

    function id(prefix) {
        return prefix + "-" + crypto.randomUUID();
    }

    function track(eventType, details) {
        if (localStorage.getItem(consentKey) !== "granted") {
            return;
        }

        const payload = Object.assign({
            eventType: eventType,
            visitorId: localStorage.getItem(visitorKey) || id("visitor"),
            sessionId: sessionStorage.getItem(sessionKey) || id("session"),
            pageUrl: window.location.href,
            referrer: document.referrer,
            consentGiven: true
        }, details || {});

        localStorage.setItem(visitorKey, payload.visitorId);
        sessionStorage.setItem(sessionKey, payload.sessionId);
        fetch(endpoint, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-LeadPulse-Api-Key": apiKey
            },
            body: JSON.stringify(payload),
            keepalive: true
        }).catch(function () {
            // Tracking must never interrupt the host website.
        });
    }

    window.LeadPulse = { track: track };
    window.LeadPulse.identify = function (details) {
        if (localStorage.getItem(consentKey) !== "granted") {
            return Promise.reject(new Error("Tracking consent is required."));
        }
        return fetch(endpoint.replace(/\/events$/, "/identify"), {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-LeadPulse-Api-Key": apiKey
            },
            body: JSON.stringify(Object.assign({
                visitorId: localStorage.getItem(visitorKey),
                consentGiven: true
            }, details || {}))
        });
    };
    window.addEventListener("load", function () {
        track("PAGE_VIEW");
    });
})();
