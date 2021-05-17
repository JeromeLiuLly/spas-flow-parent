package com.candao.spas.flow.sample.flow.bean;

import com.candao.spas.flow.jackson.EasyJsonUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TempProject {

    private String projectName;
    private TempTeacher teacher;
    private List<String> teachersSN;
    private Integer studentCount;

    @Setter
    @Getter
    public static class TempTeacher{
        private String number;
        private String userName;
    }

    public static void main(String[] args) {
        String json = "{\"projectName\":\"1140\",\"teacher\":{\"number\":\"T1140120101\",\"userName\":\"张小小\"},\"teachersSN\":[\"T1140120102\",\"T1140120103\",\"T1140120104\"],\"studentCount\":142}";
        Map o = EasyJsonUtils.toJavaObject(json, HashMap.class);
        TempProject project = EasyJsonUtils.toJavaObject(o,TempProject.class);

        System.out.println(project.getProjectName());
    }
}
