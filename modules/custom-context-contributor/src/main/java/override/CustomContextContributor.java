package override;

import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.SingleVMPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.template.TemplateContextContributor;
import com.liferay.portal.kernel.theme.NavItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = TemplateContextContributor.class,
    property = {
        "type=THEME",
        "service.ranking:Integer=" + Integer.MAX_VALUE
    }
)
public class CustomContextContributor implements TemplateContextContributor {

    private static final String CACHE_NAME = "CUSTOM_NAV_ITEMS_CACHE";

    private PortalCache<NavItemsCacheKey, List<NavItem>> _navItemsCache;

    @Reference
    private SingleVMPool _singleVMPool;

    @Override
    public void prepare(
            Map<String, Object> contextObjects,
            HttpServletRequest request) {

        ThemeDisplay themeDisplay =
            (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

        if (themeDisplay == null) {
            return;
        }

        long userId = themeDisplay.getUserId();

        try {
            
            if (_navItemsCache == null) {
                _navItemsCache =
                    (PortalCache<NavItemsCacheKey, List<NavItem>>) (PortalCache<?, ?>)
                        _singleVMPool.getPortalCache(CACHE_NAME);
            }

			NavItemsCacheKey key = new NavItemsCacheKey(userId, themeDisplay.getScopeGroupId());

			List<NavItem> navItems = _navItemsCache.get(key);

			if (navItems == null) {
				//System.out.println("Not found in cache");
				navItems = themeDisplay.getNavItems();
				 if (navItems != null && !navItems.isEmpty()) {
					//System.out.println("Put in cache");
					_navItemsCache.put(key, navItems);
				 }
			}
			contextObjects.put("navItems", navItems);

        }
        catch (Exception e) {
            _log.error("Error generating navItems", e);
        }
    }

    private static final Log _log =
        LogFactoryUtil.getLog(CustomContextContributor.class);

}
