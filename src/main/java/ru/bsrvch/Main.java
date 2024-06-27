package ru.bsrvch;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        long time = System.currentTimeMillis();

        InputStream inputStream = Main.class.getResourceAsStream("/lng.txt");
        Scanner scanner = new Scanner(inputStream);
        HashSet<String> uniqueLines = new HashSet<>();
        while (scanner.hasNextLine()) {
            String str = scanner.nextLine();
            uniqueLines.add(str);
        }
        List<Set<String>> groups = groupLines(uniqueLines);
        writeGroupsToFile(groups);
        System.out.println(System.currentTimeMillis()-time);
    }
    private static List<Set<String>> groupLines(Set<String> lines) {
        List<Set<String>> groups = new ArrayList<>();
        Map<String, Set<String>> columnValueToGroupMap = new HashMap<>();

        for (String line : lines) {
            String[] values = line.split(";");
            for(int i = 0; i < values.length; i++) {
                if(!values[i].isEmpty())
                    values[i] = i+"|"+values[i];
            }
            Set<String> group = new HashSet<>();
            boolean newGroup = true;
            for (String value : values) {
                if (!value.isEmpty() && columnValueToGroupMap.containsKey(value)) {
                    group = columnValueToGroupMap.get(value);
                    newGroup = false;
                    break;
                }
            }
            group.add(line);
            if (newGroup) {
                groups.add(group);
            }
            for (String value : values) {
                if (!value.isEmpty()) {
                    columnValueToGroupMap.put(value, group);
                }
            }
        }
        groups.sort((g1, g2) -> Integer.compare(g2.size(), g1.size()));
        return groups;
    }
    private static void writeGroupsToFile(List<Set<String>> groups) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("output.txt"))) {
            writer.println("Count of groups with more than one element: " + groups.stream().filter(g -> g.size() > 1).count());
            int groupNumber = 1;
            for (Set<String> group : groups) {
                if(group.size() > 1){
                    writer.println("Group " + groupNumber++);
                    for (String line : group) {
                        writer.println(line);
                    }
                    writer.println();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
