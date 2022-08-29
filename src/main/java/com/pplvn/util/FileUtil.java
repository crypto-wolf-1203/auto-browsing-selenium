package com.pplvn.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtil {
    public static List<String> readAsin(String path) throws IOException {
        List<String> list = null;
        Stream<String> lines = Files.lines(Paths.get(path));
        list = lines.collect(Collectors.toList());
        return list;
    }
}

