package io.bigdata;

import calculation.ListGeneric;
import calculation.UnitStringSorter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;

public class SortFiles {
    private String baseDir;
    private Predicate<String> fileFilter;
    private int[] sortColumns;
    private boolean[] sortAscending;
    private boolean withHeader;

    public SortFiles() {
    }

    public SortFiles setBaseDir(String baseDir) {
        this.baseDir = baseDir; return this;
    }

    public SortFiles setFileFilter(Predicate<String> fileFilter) {
        this.fileFilter = fileFilter; return this;
    }

    public SortFiles setSortColumns(int[] sortColumns) {
        this.sortColumns = sortColumns; return this;
    }

    public SortFiles setSortAscending(boolean[] sortAscending) {
        this.sortAscending = sortAscending; return this;
    }

    public SortFiles setWithHeader(boolean withHeader){
        this.withHeader = withHeader; return this;
    }

    public void go() throws IOException {
        File dir = new File(baseDir);
        List<String> files = ListGeneric.filter(Arrays.asList(dir.list()), fileFilter);
        UnitStringSorter fileSorter = new UnitStringSorter(sortColumns, sortAscending);

        for(String file : files){
            File dataFile = new File(baseDir + "\\" + file);
            File tmpFile = new File(baseDir + "\\" + file + "" + "TMP");

            List<String> lines = Files.readAllLines(dataFile.toPath());
            try(PrintWriter writer = new PrintWriter(tmpFile.toString())){
                if(withHeader)
                    writer.write(lines.get(0) + "\n");
                lines = lines.subList(withHeader ? 1 : 0, lines.size());
                lines.sort(fileSorter);
                for(String line : lines){
                    writer.write(line); writer.write("\n");
                }
            }

            Files.deleteIfExists(dataFile.toPath());
            tmpFile.renameTo(dataFile);
        }
    }

    public static void main(String[] args) throws IOException {
        SortFiles sortFiles = new SortFiles();
        sortFiles.setBaseDir("H:\\UpanSky\\DEDS_DenmarkAIS")
                .setFileFilter(s->s.startsWith("aisdk"))
                .setWithHeader(true)
                .setSortColumns(new int[]{2,0})
                .setSortAscending(new boolean[]{true,true});

        sortFiles.go();

    }
}
