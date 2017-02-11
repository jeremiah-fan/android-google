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

package com.google.engedu.ghost;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;
    private ArrayList<String> even;
    private ArrayList<String> odd;
    private Random random = new Random();

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        even = new ArrayList<>();
        odd = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH) {
                words.add(line.trim());
                if(word.length() % 2 == 0) // Even length
                    even.add(word);
                else
                    odd.add(word);
            }
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {
        if (prefix.equals(""))
            return words.get(random.nextInt(words.size()));
        return getGoodWordStartingWith(prefix);
    }

    @Override
    public String getGoodWordStartingWith(String prefix) {
        // Try to match parity of prefix
        String word;
        Log.d("Prefix", prefix);
        if(prefix.length() % 2 == 0) {
            word = binarySearch(even, prefix);
            if(word != null)
                Log.d("Even Search Results", word);
            return word != null ? word : binarySearch(odd, prefix);
        }else{
            word = binarySearch(odd, prefix);
            if(word != null)
                Log.d("Odd Search Results", word);
            return word != null ? word : binarySearch(even, prefix);
        }
    }

    private String binarySearch(ArrayList<String> dictionary, String prefix){
        int begin = 0;
        int end = dictionary.size() - 1;
        while (begin <= end) {
            int mid = (begin + end) / 2;
            String word = dictionary.get(mid);
            if (word.startsWith(prefix))
                return word;
            else if (word.compareTo(prefix) < 0 )
                begin = mid + 1;
            else
                end = mid - 1;
        }

        return null;
    }

    private String smartBinarySearch(ArrayList<String> dictionary, String prefix){
        int begin = 0;
        int end = words.size() - 1;
        while (begin <= end) {
            int mid = (begin + end) / 2;
            String word = words.get(mid);
            if (word.startsWith(prefix))
                return word;
            else if (word.compareTo(prefix) < 0 )
                begin = mid + 1;
            else
                end = mid - 1;
        }

        return null;
    }
}
