package com.liferay.common.api.permissionchecker.wrapper;

import com.liferay.common.api.permissionchecker.CustomDataPermissionCheckerWrapper;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.wrapper.PermissionCheckerWrapperFactory;
import com.liferay.portal.kernel.service.LayoutLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = "service.ranking:Integer=100",
	service = PermissionCheckerWrapperFactory.class
)
public class PermissionCheckerFactoryImpl
	implements PermissionCheckerWrapperFactory {

	@Override
	public PermissionChecker wrapPermissionChecker(
		PermissionChecker permissionChecker) {

		return new CustomDataPermissionCheckerWrapper(
			permissionChecker, _layoutLocalService);
	}


	@Reference
	private LayoutLocalService _layoutLocalService;
}