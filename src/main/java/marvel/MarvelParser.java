/*
 * Copyright (C) 2020 Kevin Zatloukal.  All rights reserved.  Permission is
 * hereby granted to students registered for University of Washington
 * CSE 331 for use solely during Spring Quarter 2020 for purposes of
 * the course.  No other use, copying, distribution, or modification
 * is permitted without prior written consent. Copyrights for
 * third-party components of this work must be honored.  Instructors
 * interested in reusing these course materials should contact the
 * author.
 */

package marvel;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Parser utility to load the Marvel Comics dataset.
 */
public class MarvelParser {

    /**
     * Reads the Marvel Universe dataset. Each line of the input file contains a character name and a
     * comic book the character appeared in, separated by a tab character
     *
     * @param filename the file that will be read
     * @spec.requires filename is a valid file in the resources/data folder.
     * @return a HashMap with books as key and a set of characters as values.
     */
    public static HashMap<String, Set<String>> parseData(String filename) {
        // You can use this code as an example for getting a file from the resources folder
        // in a project like this. If you access TSV files elsewhere in your code, you'll need
        // to use similar code. If you use this code elsewhere, don't forget:
        //   - Replace 'MarvelParser' in `MarvelParser.class' with the name of the class you write this in
        //   - If the class is in src/main, it'll get resources from src/main/resources
        //   - If the class is in src/test, it'll get resources from src/test/resources
        //   - The "/" at the beginning of the path is important
        // Note: Most students won't re-write this code anywhere, this explanation is just for completeness.
        InputStream stream = MarvelParser.class.getResourceAsStream("/data/" + filename);
        if(stream == null) {
            // stream is null if the file doesn't exist.
            // You'll probably want to handle this case so you don't try to call
            // getPath and have a null pointer exception.
            // Technically, you'd be allowed to just have the NPE because of
            // the @spec.requires, but it's good to program defensively. :)
            throw new IllegalArgumentException("provided an invalid file name");
        }
        Reader reader = new BufferedReader(new InputStreamReader(stream));

        Iterator<CharacterModel> csvUserIterator =
                new CsvToBeanBuilder<CharacterModel>(reader)
                        .withType(CharacterModel.class)
                        .withSeparator('\t')
                        .withIgnoreLeadingWhiteSpace(true)
                        .build()
                        .iterator();

        HashMap<String, Set<String>> bookToCharacterMap = new HashMap<>();

        while(csvUserIterator.hasNext()) {
            CharacterModel temp = csvUserIterator.next();
            Set<String> set = new HashSet<>();
            set.add(temp.getHero());
            bookToCharacterMap.merge(temp.getBook(), set, (prev, val) -> {
                prev.add(temp.getHero());
                return prev;
            });
        }
        return bookToCharacterMap;
    }


    /**
     * An inner (javabean) class that models a line from tsv file
     *
     * @spec.specfield hero     : String  // data under column "hero"
     * @spec.specfield book     : String  // data under column "book"
     *
     * Abstract Invariant:
     *  hero and book exists in pair, there is no null value
     */
    public static class CharacterModel {
        @CsvBindByName
        private String hero;

        @CsvBindByName
        private String book;

        public String getHero() {
            return hero;
        }

        public String getBook() {
            return book;
        }

        public void setHero(String hero) {
            this.hero = hero;
        }

        public void setBook(String book) {
            this.book = book;
        }
    }
}

