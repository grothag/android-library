/* ownCloud Android Library is available under MIT license
 *   Copyright (C) 2016 ownCloud GmbH.
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *   EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *   MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 *   BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 *   ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *   CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 *
 */
package com.owncloud.android.lib.common.authentication;

import org.apache.commons.httpclient.cookie.CookiePolicy;

import android.net.Uri;

import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.http.HttpClient;
import com.owncloud.android.lib.common.http.interceptors.BarearAuthInterceptor;
import com.owncloud.android.lib.common.http.interceptors.BasicAuthInterceptor;
import com.owncloud.android.lib.common.http.interceptors.HttpInterceptor;
import com.owncloud.android.lib.common.http.interceptors.SamlAuthInterceptor;

import java.util.ArrayList;

import okhttp3.Cookie;

public class OwnCloudSamlSsoCredentials implements OwnCloudCredentials {

    private String mUsername;
    private String mSessionCookie;

    public OwnCloudSamlSsoCredentials(String username, String sessionCookie) {
        mUsername = username != null ? username : "";
        mSessionCookie = sessionCookie != null ? sessionCookie : "";
    }

    @Override
    public void applyTo(OwnCloudClient client) {
        final ArrayList<HttpInterceptor.RequestInterceptor> requestInterceptors =
                HttpClient.getOkHttpInterceptor().getRequestInterceptors();

        // Clear previous basic credentials
        for (HttpInterceptor.RequestInterceptor requestInterceptor : requestInterceptors) {
            if (requestInterceptor instanceof BasicAuthInterceptor
                    || requestInterceptor instanceof BarearAuthInterceptor
                    || requestInterceptor instanceof SamlAuthInterceptor) {
                requestInterceptors.remove(requestInterceptor);
            }
        }

        HttpClient.getOkHttpInterceptor()
                .addRequestInterceptor(new SamlAuthInterceptor(mSessionCookie));
    }

    @Override
    public String getUsername() {
        // not relevant for authentication, but relevant for informational purposes
        return mUsername;
    }

    @Override
    public String getAuthToken() {
        return mSessionCookie;
    }

    @Override
    public boolean authTokenExpires() {
        return true;
    }

    @Override
    public boolean authTokenCanBeRefreshed() {
        return false;
    }

}
