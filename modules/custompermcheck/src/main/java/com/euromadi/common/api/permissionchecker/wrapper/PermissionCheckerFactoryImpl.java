package com.euromadi.common.api.permissionchecker.wrapper;

import com.euromadi.common.api.permissionchecker.CustomDataPermissionCheckerWrapper;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.wrapper.PermissionCheckerWrapperFactory;

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
			permissionChecker, _dlFileEntryLocalService);
	}


	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;
}