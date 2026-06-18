package com.aether.tunnel.plugins;

import android.content.Context;
import android.content.Intent;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.aether.tunnel.services.V2RayVpnService;

@CapacitorPlugin(name = "XrayPlugin")
public class XrayBridge extends Plugin {

    @PluginMethod
    public void startTunnel(PluginCall call) {
        String configJson = call.getString("config");
        Context context = getContext();
        
        Intent intent = new Intent(context, V2RayVpnService.class);
        intent.setAction("START_VPN");
        intent.putExtra("CONFIG_DATA", configJson);
        context.startService(intent);
        
        JSObject ret = new JSObject();
        ret.put("status", "initiating");
        call.resolve(ret);
    }

    @PluginMethod
    public void stopTunnel(PluginCall call) {
        Context context = getContext();
        Intent intent = new Intent(context, V2RayVpnService.class);
        intent.setAction("STOP_VPN");
        context.startService(intent);
        
        JSObject ret = new JSObject();
        ret.put("status", "stopped");
        call.resolve(ret);
    }
}
