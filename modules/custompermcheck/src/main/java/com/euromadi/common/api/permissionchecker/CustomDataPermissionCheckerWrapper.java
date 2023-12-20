package com.euromadi.common.api.permissionchecker;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.wrapper.PermissionCheckerWrapper;
import com.liferay.portal.kernel.util.StringUtil;

public class CustomDataPermissionCheckerWrapper extends PermissionCheckerWrapper {
    public CustomDataPermissionCheckerWrapper(PermissionChecker permissionChecker, DLFileEntryLocalService dlFileEntryLocalService) {
        super(permissionChecker);
        _dlFileEntryLocalService = dlFileEntryLocalService;
    }
    @Override
    public boolean hasPermission(Group group, String name, long primKey, String actionId) {
        return super.hasPermission(group, name, primKey, actionId) && customHasPermission(name, primKey, actionId);
    }
    private boolean customHasPermission(String name, long primKey, String actionId) {
        System.out.println("actionId --->" + actionId + " primKey --->" + primKey + " userId --->" + getUserId());
        if (StringUtil.equals(name, DLFileEntry.class.getName())) {
            DLFileEntry dlFileEntry = _dlFileEntryLocalService.fetchDLFileEntry(primKey);
            if (dlFileEntry != null) {
                System.out.println("Es DlFileEntry primkey--->" + primKey);
                if (StringUtil.equals(getUser().getScreenName(), "test User Sarrion")) {
                    System.out.println("Retornamos false");
                    return false;
                }
            }
        } else if (StringUtil.equals(name, DLFolder.class.getName())) {
            System.out.println("Es DLFolder primkey--->" + primKey);
            if (StringUtil.equals(getUser().getScreenName(), "test User Sarrion")) {
                System.out.println("Retornamos false");
                return false;
            }
        } else {
            System.out.println("No es Documento: " + name);
        }
        return true;
    }
    
    private final DLFileEntryLocalService _dlFileEntryLocalService;
}
