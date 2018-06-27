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

import android.app.DownloadManager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.auth.AuthState;

import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.authentication.oauth.BearerAuthScheme;
import com.owncloud.android.lib.common.authentication.oauth.BearerCredentials;
import com.owncloud.android.lib.common.http.HttpClient;
import com.owncloud.android.lib.common.http.interceptors.BarearAuthInterceptor;
import com.owncloud.android.lib.common.http.interceptors.BasicAuthInterceptor;
import com.owncloud.android.lib.common.http.interceptors.HttpInterceptor;
import com.owncloud.android.lib.common.http.interceptors.SamlAuthInterceptor;

public class OwnCloudBearerCredentials implements OwnCloudCredentials {

    private String mUsername;
    private String mAccessToken;

    public OwnCloudBearerCredentials(String username, String accessToken) {
        mUsername = username != null ? username : "";
        mAccessToken = accessToken != null ? accessToken : "";
    }

    @Override
    public void applyTo(OwnCloudClient client) {
        AuthPolicy.registerAuthScheme(BearerAuthScheme.AUTH_POLICY, BearerAuthScheme.class);
        AuthPolicy.registerAuthScheme(AuthState.PREEMPTIVE_AUTH_SCHEME, BearerAuthScheme.class);

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
                .addRequestInterceptor(new BarearAuthInterceptor(mAccessToken));
    }

    @Override
    public String getUsername() {
        // not relevant for authentication, but relevant for informational purposes
        return mUsername;
    }

    @Override
    public String getAuthToken() {
        return mAccessToken;
    }

    @Override
    public boolean authTokenExpires() {
        return true;
    }

    @Override
    public boolean authTokenCanBeRefreshed() {
        return true;
    }

}
