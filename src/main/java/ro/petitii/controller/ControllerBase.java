package ro.petitii.controller;

import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

public abstract class ControllerBase {
    enum ToastType {
        success, info, warning, danger
    }

    public ModelAndView createToast(ModelAndView modelAndView, String message, ToastType type) {
        Map<String, String> toast = new HashMap<>();
        toast.put("message", message);
        toast.put("type", "alert-" + type.name());
        modelAndView.addObject("toastContent", toast);
        return modelAndView;
    }
}