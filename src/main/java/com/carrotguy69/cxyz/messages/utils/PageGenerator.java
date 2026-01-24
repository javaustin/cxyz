package com.carrotguy69.cxyz.messages.utils;

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


    public String generatePage(int pageNumber) {
        /*

        [!] Using 1-based indexing instead of 0-based (page numbers start at 1 instead of 0)

        ex:
        let n = 18 (total entries)
        let m = 4 (max entries per page)
        let p = specified page number

        so:
        1 -> [0, 3]
        2 -> [4, 7]
        3 -> [8, 11]
        4 -> [12, 15]
        5 -> [16, 17]


        start = (p - 1) * m
        end (if full page) = (p * m) - 1
        end (if not full page) = n - 1

        available pages: n / m -> 18/4 = 4

        full page available if: (n / m) > p
        */

        if (pageNumber <= 0) {
            pageNumber = 1;
        }

        int size = entries.size();

        int availablePages = pageNumber / maxEntriesPerPage + 1;

        if (pageNumber > availablePages) {
            pageNumber = availablePages;
        }

        int startIndex = (pageNumber - 1) * maxEntriesPerPage;

        boolean fullPageAvailable = (size / maxEntriesPerPage) > pageNumber;

        int endIndex;
        if (fullPageAvailable)
            endIndex = (pageNumber * maxEntriesPerPage) - 1;

        else
            endIndex = size - 1;

        return String.join(delimiter, entries.subList(startIndex, endIndex));
    }
}
