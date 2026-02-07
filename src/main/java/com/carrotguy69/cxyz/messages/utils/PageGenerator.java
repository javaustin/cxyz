package com.carrotguy69.cxyz.messages.utils;

import com.carrotguy69.cxyz.other.Logger;

import java.util.List;

public class PageGenerator {
    private final List<String> entries;
    private final String delimiter;
    private final int maxEntriesPerPage;

    public PageGenerator(List<String> entries, String delimiter, int maxEntriesPerPage) {
        this.entries = entries;
        this.delimiter = delimiter;
        this.maxEntriesPerPage = maxEntriesPerPage;
    }

    public List<String> getEntries() {
        return entries;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public int getMaxEntriesPerPage() {
        return maxEntriesPerPage;
    }

    public int getMaxPages() {
        return (int) Math.ceil((double) entries.size() / (double) maxEntriesPerPage);
    }


    public String generatePage(int pageNumber) {
        /*

        [!] Using 1-based indexing instead of 0-based (page numbers start at 1 instead of 0)

        ex:
        let n = 21 (total entries)
        let m = 5 (max entries per page)
        let p = specified page number

        so:
        1 -> [0, 4]
        2 -> [5, 9]
        3 -> [10, 14]
        4 -> [15, 19]
        5 -> [20, 20] (1 entry leftover)

        for each page:
            start: (p - 1) * m
            end: min((p * m) - 1, n -1)

        available pages: ceil(double n / double m) -> ceil(21 / 5) -> ceil(4.1) -> 5
        full page available if: available pages > p
        half page available if: available pages == p
        no page if: available pages < p

        */

        int size = entries.size();

        int startIndex = (pageNumber - 1) * maxEntriesPerPage;
        int endIndex = Math.min((pageNumber * maxEntriesPerPage) - 1, size - 1);

        return String.join(delimiter, entries.subList(startIndex, endIndex + 1));
    }
}
