/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();
    private int wordLength = DEFAULT_WORD_LENGTH;
    private ArrayList<String> wordList = new ArrayList<String>();
    private HashSet<String> wordSet = new HashSet<String>();
    private HashMap<String, ArrayList<String>> lettersToWord = new HashMap<String, ArrayList<String>>();
    private HashMap<Integer, ArrayList<String>> sizeToWords = new HashMap<Integer, ArrayList<String>>();

    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            wordList.add(word);
            wordSet.add(word);

            String sorted = sortLetters(word);
            if (!lettersToWord.containsKey(sorted))
                lettersToWord.put(sorted, new ArrayList<String>());
            lettersToWord.get(sorted).add(word);

            if (!sizeToWords.containsKey(word.length()))
                sizeToWords.put(word.length(), new ArrayList<String>());
            sizeToWords.get(word.length()).add(word);
        }
    }

    public boolean isGoodWord(String word, String base) {
        return wordSet.contains(word) && !word.contains(base);
    }

    public List<String> getAnagrams(String targetWord){
        String sortedtarget = sortLetters(targetWord);

        ArrayList<String> result = new ArrayList<String>();
        for(int i = 0; i < wordList.size(); i++){
            String original = wordList.get(i);
            String sorted = sortLetters(original);
            if(sortedtarget.length() == sorted.length() && sortedtarget.equals(sorted))
                result.add(original);
        }
        return result;
    }

    public List<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<String>();
        final int ALPHABET_SIZE = 26;
        for(int i = 0; i < ALPHABET_SIZE; i++){
            String key = sortLetters(word + (char)('a' + i));
            if(lettersToWord.containsKey(key))
                for(String anagram: lettersToWord.get(key))
                    if(isGoodWord(anagram, word))
                        result.add(anagram);
        }
        return result;
    }

    public String pickGoodStarterWord() {
        ArrayList<String> smallerlist = sizeToWords.get(wordLength);
        int start = random.nextInt(smallerlist.size());
        while(getAnagramsWithOneMoreLetter(smallerlist.get(start)).size() < MIN_NUM_ANAGRAMS){
            start = (start + 1) % smallerlist.size();
        }
        if(wordLength < MAX_WORD_LENGTH)
            wordLength++;
        return smallerlist.get(start);
    }

    private String sortLetters(String toSort){
        if (toSort.equals(""))
            return "";
        char[] chars = toSort.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }
}
