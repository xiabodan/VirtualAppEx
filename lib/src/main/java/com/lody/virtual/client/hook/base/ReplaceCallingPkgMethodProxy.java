package com.lody.virtual.client.hook.base;

import java.lang.reflect.Method;

import com.lody.virtual.client.hook.utils.MethodParameterUtils;
import com.lody.virtual.helper.utils.VLog;

/**
 * @author Lody
 */

public class ReplaceCallingPkgMethodProxy extends StaticMethodProxy {

	public ReplaceCallingPkgMethodProxy(String name) {
		super(name);
	}

	@Override
	public boolean beforeCall(Object who, Method method, Object... args) {
		String name = MethodParameterUtils.replaceFirstAppPkg(args);
		VLog.v(method.getName(), "replaceFirstAppPkg " + name);
		return super.beforeCall(who, method, args);
	}
}
