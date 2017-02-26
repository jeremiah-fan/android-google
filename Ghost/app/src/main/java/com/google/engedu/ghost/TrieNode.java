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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Stack;


public class TrieNode {
    private HashMap<String, TrieNode> children;
    private boolean isWord;

    public TrieNode() {
        children = new HashMap<>();
        isWord = false;
    }

    public void add(String s) {
        HashMap <String, TrieNode> temp = children;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!temp.containsKey("" + c))
                temp.put("" + c, new TrieNode());

            if (i != s.length() - 1)
                temp = temp.get("" + c).children;
            else
                temp.get("" + c).isWord = true;
        }
    }

    public boolean isWord(String s) {
        TrieNode search = searchNode(s);
        return search != null && search.isWord;
    }

    public String getAnyWordStartingWith(String s) {
        TrieNode search = searchNode(s);
        if(search == null)
            return null;

        Random rand = new Random();
        HashMap <String, TrieNode> cur = search.children;
        while(!cur.isEmpty()) { //When cur.isEmpty() is true, we have reached a leaf node
            List <String> letterSet = new ArrayList<>(cur.keySet());
            String letterToAdd = letterSet.get(rand.nextInt(letterSet.size()));
            s += letterToAdd;

            if(cur.get(letterToAdd).isWord && rand.nextInt(2) == 1) // If is word, randomly choose if want to keep going or stop
                break;

            cur = cur.get(letterToAdd).children;
        }

        return s;
    }

    public String getGoodWordStartingWith(String s) {
        if(s.equals(""))
            return getAnyWordStartingWith("");

        TrieNode search = searchNode(s);
        if(search == null)
            return null;

        Random rand = new Random();
        HashMap <String, TrieNode> cur = search.children;
        while(!cur.isEmpty()) {
            List <String> badWords = new ArrayList<>();
            List <String> okayWords = new ArrayList<>();

            String letterToAdd = "";
            for(String letter: new ArrayList<>(cur.keySet())) {
                if(!cur.get(letter).isWord) {
                    if (DFSFindGoodWord(cur.get(letter), s)) {
                        letterToAdd = letter;
                        break;
                    } else {
                        okayWords.add(letter);
                    }
                }
                else {
                    badWords.add(letter);
                }
            }

            if(letterToAdd.equals(""))
                letterToAdd = !okayWords.isEmpty() ? okayWords.get(rand.nextInt(okayWords.size())) : badWords.get(rand.nextInt(badWords.size()));
            s += letterToAdd;
            cur = cur.get(letterToAdd).children;
        }
        return s;
    }

    private TrieNode searchNode(String s){ // Returns last TrieNode of matching string, if it exists
        HashMap <String, TrieNode> cur = children;
        for(int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!cur.containsKey("" + c))
                return null;

            if (i != s.length() - 1)
                cur = cur.get("" + c).children;
            else
                return cur.get("" + c);
        }
        return this; // for empty string
    }

    private boolean DFSFindGoodWord(TrieNode startingPoint, String initString){
        Stack<TrieNode> toVisit = new Stack<>();
        Stack<Integer> toVisitLength = new Stack<>();
        for(TrieNode t : startingPoint.children.values()) {
            toVisit.push(t);
            toVisitLength.push(1);
        }

        while (!toVisit.isEmpty() && !toVisitLength.isEmpty()){
            TrieNode t = toVisit.pop();
            int length = toVisitLength.pop();

            if(t.children.isEmpty() && initString.length() % 2 == length % 2)
                return true;

            for (TrieNode subT : t.children.values()){
                if(initString.length() % 2 != length % 2 || !subT.isWord) {
                    toVisit.push(subT);
                    toVisitLength.push(length + 1);
                }
            }
        }

        return false;
    }
}
