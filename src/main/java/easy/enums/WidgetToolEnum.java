package easy.enums;

import easy.widget.annotation.*;

public enum WidgetToolEnum {

    JACKSON("Jackson", new DoJacksonAnnotationService()),
    GSON("Gson", new DoGsonAnnotationService()),
    FASTJSON("FastJson", new DoFastJsonAnnotationService()),
    EASY_EXCEL("EasyExcel", new DoEasyExcelAnnotationService());

    private final String name;
    private final DoAnnotationService annotationService;

    WidgetToolEnum(String name, DoAnnotationService annotationService) {
        this.name = name;
        this.annotationService = annotationService;
    }

    public String getName() {
        return name;
    }

    public DoAnnotationService getAnnotationService() {
        return annotationService;
    }

    public static WidgetToolEnum getEnum(String name) {
        for (WidgetToolEnum widgetToolEnum : WidgetToolEnum.values()) {
            if (widgetToolEnum.getName().equals(name)) {
                return widgetToolEnum;
            }
        }
        return null;
    }

}
