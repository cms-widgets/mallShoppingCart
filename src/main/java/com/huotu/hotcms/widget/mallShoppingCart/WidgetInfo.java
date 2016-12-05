/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.hotcms.widget.mallShoppingCart;

import com.huotu.hotcms.service.service.MallService;
import com.huotu.hotcms.widget.*;
import com.huotu.huobanplus.common.entity.User;
import me.jiangcai.lib.resource.service.ResourceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * @author CJ
 */
public class WidgetInfo implements Widget, PreProcessWidget {
    public static final Log log = LogFactory.getLog(WidgetInfo.class);

    public static final String SHOPPING_CART_COUNT = "shoppingCartCount";
    public static final String USER_ID = "userId";
    public static final String LOGIN_STATE = "loginState";

    @Override
    public String groupId() {
        return "com.huotu.hotcms.widget.mallShoppingCart";
    }

    @Override
    public String widgetId() {
        return "mallShoppingCart";
    }

    @Override
    public String name(Locale locale) {
        if (locale.equals(Locale.CHINA)) {
            return "购物车组件";
        }
        return "mallShoppingCart";
    }

    @Override
    public String description(Locale locale) {
        if (locale.equals(Locale.CHINA)) {
            return "这是一个购物车组件，你可以对组件进行自定义修改。";
        }
        return "This is a mallShoppingCart,  you can make custom change the component.";
    }

    @Override
    public String dependVersion() {
        return "1.0";
    }

    @Override
    public WidgetStyle[] styles() {
        return new WidgetStyle[]{new DefaultWidgetStyle()};
    }

    @Override
    public Resource widgetDependencyContent(MediaType mediaType) {
        if (mediaType.equals(Widget.Javascript))
            return new ClassPathResource("js/widgetInfo.js", getClass().getClassLoader());
        return null;
    }

    @Override
    public Map<String, Resource> publicResources() {
        Map<String, Resource> map = new HashMap<>();
        map.put("thumbnail/defaultStyleThumbnail.png", new ClassPathResource("thumbnail/defaultStyleThumbnail.png"
                , getClass().getClassLoader()));
        return map;
    }

    @Override
    public void valid(String styleId, ComponentProperties componentProperties) throws IllegalArgumentException {
        WidgetStyle style = WidgetStyle.styleByID(this, styleId);
        //加入控件独有的属性验证

    }

    @Override
    public Class springConfigClass() {
        return null;
    }

    @Override
    public ComponentProperties defaultProperties(ResourceService resourceService) throws IOException {
        ComponentProperties properties = new ComponentProperties();
        return properties;
    }

    @Override
    public boolean disabled() {
        return CMSContext.RequestContext().getSite().getOwner() != null && CMSContext.RequestContext().getSite().getOwner().getCustomerId() != null;
    }

    @Override
    public void prepareContext(WidgetStyle style, ComponentProperties properties, Map<String, Object> variables
            , Map<String, String> parameters) {
        MallService mallService = getCMSServiceFromCMSContext(MallService.class);
        User user = null;
        try {
            user = mallService.getLoginUser(CMSContext.RequestContext().getRequest(), CMSContext.RequestContext().getSite().getOwner());
        } catch (Throwable e) {
            log.error("通讯异常导致无法获取", e);
        }
        if (user != null) {
            variables.put(SHOPPING_CART_COUNT, 0);
            variables.put(USER_ID, user.getId());
            variables.put(LOGIN_STATE, true);
        } else {
            variables.put(SHOPPING_CART_COUNT, 0);
            variables.put(USER_ID, null);
            variables.put(LOGIN_STATE, false);
        }
        String domain;
        try {
            domain = mallService.getMallDomain(CMSContext.RequestContext().getSite().getOwner());
            domain = "http://" + domain + "/Mall/Cart/" + CMSContext.RequestContext().getSite().getOwner().getCustomerId();
            variables.put("carUrl", domain);
        } catch (Throwable e) {
            log.error("通讯异常", e);
            variables.put("carUrl", "#");
        }

    }

}
