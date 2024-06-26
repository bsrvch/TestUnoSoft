package ru.bsrvch;

import java.io.InputStream;
import java.util.*;
import java.io.*;
import java.util.stream.Collectors;
public class Main {

    public static void main(String[] args) {

        InputStream inputStream = Main.class.getResourceAsStream("/lng.txt");
        Scanner scanner = new Scanner(inputStream);
        HashSet<String> uniqueLines = new HashSet<>();
        while (scanner.hasNextLine()) {
            String str = scanner.nextLine();
            uniqueLines.add(str);
        }
        List<Set<String>> groups = groupLines(uniqueLines);
        writeGroupsToFile(groups);
    }
    private static List<Set<String>> groupLines(Set<String> lines) {
        DSU uf = new DSU(lines.size());
        Map<String, Integer> lineToIndex = new HashMap<>();
        int index = 0;

        List<String[]> parsedLines = new ArrayList<>();
        for(String line : lines){
            lineToIndex.put(line, index++);
            String[] strs = line.replace("\"","").split(";");
            for (int i = 0; i < strs.length; i++) {
                if(!strs[i].isEmpty()){
                    strs[i] = i +"i" + strs[i];
                }
            }
            parsedLines.add(strs);
        }
        Map<String,HashSet<Integer>> valueToIndexesMap = new HashMap<String,HashSet<Integer>>();
        for (int i = 0; i < parsedLines.size(); i++) {
            for(String str: parsedLines.get(i)){
                if(!str.isEmpty())
                    valueToIndexesMap.computeIfAbsent(str, x-> new HashSet<>()).add(i);
            }
        }

        for (Set<Integer> indexes : valueToIndexesMap.values()) {
            List<Integer> indexList = new ArrayList<>(indexes);
            for (int i = 1; i < indexList.size(); i++) {
                uf.union(indexList.getFirst(), indexList.get(i));
            }
        }

        Map<Integer, Set<String>> indexToGroup = new HashMap<>();
        for (String line : lines) {
            int root = uf.find(lineToIndex.get(line));
            indexToGroup.computeIfAbsent(root, k -> new HashSet<>()).add(line);
        }

        return indexToGroup.values().stream()
                .filter(group -> group.size() > 1)
                .sorted((a, b) -> Integer.compare(b.size(), a.size()))
                .collect(Collectors.toList());
    }
    private static void writeGroupsToFile(List<Set<String>> groups) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("output.txt"))) {
            writer.println("Count of groups with more than one element: " + groups.size());
            int groupNumber = 1;
            for (Set<String> group : groups) {
                writer.println("Group " + groupNumber++);
                for (String line : group) {
                    writer.println(line);
                }
                writer.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}