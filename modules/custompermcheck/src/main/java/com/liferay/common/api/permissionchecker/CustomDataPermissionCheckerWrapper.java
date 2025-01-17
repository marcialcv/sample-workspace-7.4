package com.liferay.common.api.permissionchecker;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.wrapper.PermissionCheckerWrapper;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class CustomDataPermissionCheckerWrapper extends PermissionCheckerWrapper {
   
	
	private static final String FIELD_NAME = "acceso";
	
	
	public CustomDataPermissionCheckerWrapper(PermissionChecker permissionChecker, LayoutLocalService layoutLocalService) {
        super(permissionChecker);
        _layoutLocalService = layoutLocalService;
    }
    @Override
    public boolean hasPermission(Group group, String name, long primKey, String actionId) {
        return super.hasPermission(group, name, primKey, actionId) && hasRequiredLevel(name, primKey, actionId);
    }
    private boolean hasRequiredLevel(String name, long primKey, String actionId) {
    	ServiceContext serviceContext = ServiceContextThreadLocal.getServiceContext();
    	if (serviceContext != null) {
    	    HttpServletRequest request = serviceContext.getRequest();
    	    HttpSession session = request.getSession();
    	    // Ahora tienes acceso a la sesión
    	    System.out.println("ID de sesión: " + session.getId());
    	    if(session.getAttribute("accessLevel") != null) {
    	    	System.out.println("recupero de sesion "+ (long) session.getAttribute("accessLevel"));
    	    }
    	    else {
	    	    if (StringUtil.equals(name, Layout.class.getName())) {
	                Layout layout = _layoutLocalService.fetchLayout(primKey);
	                if (layout != null) {
	                	Serializable attributeValue = layout.getExpandoBridge().getAttribute(FIELD_NAME);
	                	if(layout.getExpandoBridge().getAttribute(FIELD_NAME) != null) {
	    	            	if (attributeValue instanceof long[]) {
	    	            	    long[] values = (long[]) attributeValue;
	    	            	    for (long value : values) {
	    	            	        System.out.println("Nivel de acceso setteo: " + value);
	    	            	        session.setAttribute("accessLevel", value);
	    	            	    }
	    	            	}
	                	}
	                }
	            } else {
	//                System.out.println("No es Layout: " + name);
	            }
    	    }
    	} else {
    	    System.out.println("ServiceContext no está disponible.");
    	}
       
        return true;
    }
    
    private final LayoutLocalService _layoutLocalService;
}
