/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.yoogor.plugin_manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.tencent.shadow.core.manager.installplugin.InstalledPlugin;
import com.tencent.shadow.dynamic.host.EnterCallback;


import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SamplePluginManager extends FastPluginManager {
    public static final String KEY_PLUGIN_ZIP_PATH = "pluginZipPath";
    public static final String KEY_ACTIVITY_CLASSNAME = "KEY_ACTIVITY_CLASSNAME";
    public static final String KEY_EXTRAS = "KEY_EXTRAS";
    public static final String KEY_PLUGIN_PART_KEY = "KEY_PLUGIN_PART_KEY";
    public static final String PART_KEY_PLUGIN_MAIN_APP = "sample-plugin-app";
    public static final int FROM_ID_NOOP = 1000;
    public static final int FROM_ID_START_ACTIVITY = 1002;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Context mCurrentContext;

    public SamplePluginManager(Context context) {
        super(context);
        mCurrentContext = context;
    }

    /**
     * @return PluginManager实现的别名，用于区分不同PluginManager实现的数据存储路径
     */
    @Override
    protected String getName() {
        return "test-dynamic-manager";
    }

    /**
     * @return 宿主so的ABI。插件必须和宿主使用相同的ABI。
     */
    @Override
    public String getAbi() {
        return "";
    }

    /**
     * @return 宿主中注册的PluginProcessService实现的类名
     */
    @Override
    protected String getPluginProcessServiceName() {
        return "com.yoogor.testshadow.shadow.PluginProcessPPS";
    }

    @Override
    public void enter(final Context context, long fromId, Bundle bundle, final EnterCallback callback) {
        onStartActivity(context, bundle, callback);
    }

    private void onStartActivity(final Context context, Bundle bundle, final EnterCallback callback) {
        final String pluginZipPath = bundle.getString(KEY_PLUGIN_ZIP_PATH);
        final String partKey = bundle.getString(KEY_PLUGIN_PART_KEY);
        final String className = bundle.getString(KEY_ACTIVITY_CLASSNAME);
        if (className == null) {
            throw new NullPointerException("className == null");
        }
        final Bundle extras = bundle.getBundle(KEY_EXTRAS);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Intent pluginIntent = new Intent();
                    pluginIntent.setClassName(
                            context.getPackageName(),
                            className
                    );
                    if (extras != null) {
                        pluginIntent.replaceExtras(extras);
                    }
//                    List<InstalledPlugin> installedPlugins = getInstalledPlugins(1);
//                    Iterator<InstalledPlugin> installedPluginIterator = installedPlugins.iterator();
                    InstalledPlugin installedPlugin1 = null;
//                    while (installedPluginIterator.hasNext()) {
//                        InstalledPlugin installedPlugin = installedPluginIterator.next();
//                        if (installedPlugin.plugins.containsKey(partKey) && installedPlugin.pluginLoaderFile != null && installedPlugin.runtimeFile != null) {
//                            installedPlugin1 = installedPlugin;
//                            break;
//                        }
//                    }
                    if (installedPlugin1 == null) {
                        installedPlugin1 = installPlugin(pluginZipPath, null, true);
                    }
                    startPluginActivity(context, installedPlugin1, partKey, pluginIntent);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (callback != null) {
                    callback.onCloseLoadingView();
                }
            }
        });
    }
}
