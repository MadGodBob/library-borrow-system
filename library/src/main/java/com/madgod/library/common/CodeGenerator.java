package com.madgod.library.common;

import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.model.ClassAnnotationAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Properties;

public class CodeGenerator {
    /*
        参数配置 表名 作者 工作目录 父类包名称 数据库url 数据库账号 数据库密码
    */
    public static String tableName = "borrow";
    public static String author = "madgod";
    public static String workspaceParent = "com.madgod.library";
    public static String MySQL_url;
    public static String usename;
    public static String password;

    private static Path resolveModuleRoot() {
        Path current = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();

        if (Files.exists(current.resolve(Paths.get("src", "main", "java")))) {
            return current;
        }

        Path springbootModule = current.resolve("springboot");
        if (Files.exists(springbootModule.resolve(Paths.get("src", "main", "java")))) {
            return springbootModule;
        }

        return current;
    }

    private static String pickProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private static Properties loadDatasourceFromApplicationProperties(Path moduleRoot) {
        Properties properties = new Properties();

        // Prefer classpath resource; fallback to module file for direct run scenarios.
        try (InputStream classpathInput = CodeGenerator.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (classpathInput != null) {
                properties.load(classpathInput);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read classpath application.properties", ex);
        }

        Path propertiesPath = moduleRoot.resolve(Paths.get("src", "main", "resources", "application.properties"));
        if (properties.isEmpty() && Files.exists(propertiesPath)) {
            try (InputStream fileInput = Files.newInputStream(propertiesPath)) {
                properties.load(fileInput);
            } catch (IOException ex) {
                throw new IllegalStateException("Failed to read application.properties at: " + propertiesPath, ex);
            }
        }

        return properties;
    }

    public static void main(String[] args) {
        Path moduleRoot = resolveModuleRoot();
        Properties properties = loadDatasourceFromApplicationProperties(moduleRoot);
        MySQL_url = pickProperty(properties, "spring.datasource.url");
        usename = pickProperty(properties, "spring.datasource.username");
        password = pickProperty(properties, "spring.datasource.password");
        String javaOutputDir = moduleRoot.resolve(Paths.get("src", "main", "java")).toString();
        String xmlOutputDir = moduleRoot.resolve(Paths.get("src", "main", "resources", "mapper")).toString();

        FastAutoGenerator.create(MySQL_url, usename, password)
                .globalConfig(builder ->
                        builder.author(author)
                                .disableOpenDir()
                                .outputDir(javaOutputDir)
                )
                .packageConfig(builder ->
                        builder.pathInfo(Collections.singletonMap(OutputFile.xml, xmlOutputDir))
                                .parent(workspaceParent)
                                .entity("entity")
                                .mapper("mapper")
                                .service("service")
                                .serviceImpl("service.impl")
                )
                .strategyConfig(builder ->
                        builder.addInclude(tableName)
                                .enableSkipView()
                                .entityBuilder().enableLombok(new ClassAnnotationAttributes("@Data","lombok.Data"))
                                .mapperBuilder().mapperAnnotation(Mapper.class)
                                .controllerBuilder()
                                .mapperBuilder()
                                .mapperXmlTemplate("/templates/simple-mapper.xml")
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}