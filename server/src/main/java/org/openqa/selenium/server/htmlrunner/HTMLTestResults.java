/*
 * Copyright 2004 ThoughtWorks, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.openqa.selenium.server.htmlrunner;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

/**
 * A data model class for the results of the Selenium HTMLRunner (aka TestRunner, FITRunner)
 * 
 * @author Darren Cotterill
 * @author Ajit George
 */
public class HTMLTestResults {
    private final String result;
    private final String totalTime;
    private final String numTestPasses;
    private final String numTestFailures;
    private final String numCommandPasses;
    private final String numCommandFailures;
    private final String numCommandErrors;
    private final HTMLSuiteResult suite;

    private static final String HEADER = "<html>\n" +
    "<head><style type='text/css'>\n" +
    "body, table {\n" + 
    "    font-family: Verdana, Arial, sans-serif;\n" + 
    "    font-size: 12;\n" + 
    "}\n" + 
    "\n" + 
    "table {\n" + 
    "    border-collapse: collapse;\n" + 
    "    border: 1px solid #ccc;\n" + 
    "}\n" + 
    "\n" + 
    "th, td {\n" + 
    "    padding-left: 0.3em;\n" + 
    "    padding-right: 0.3em;\n" + 
    "}\n" + 
    "\n" + 
    "a {\n" + 
    "    text-decoration: none;\n" + 
    "}\n" + 
    "\n" + 
    ".title {\n" + 
    "    font-style: italic;\n" + 
    "}\n" + 
    "\n" + 
    ".selected {\n" + 
    "    background-color: #ffffcc;\n" + 
    "}\n" + 
    "\n" + 
    ".status_done {\n" + 
    "    background-color: #eeffee;\n" + 
    "}\n" + 
    "\n" + 
    ".status_passed {\n" + 
    "    background-color: #ccffcc;\n" + 
    "}\n" + 
    "\n" + 
    ".status_failed {\n" + 
    "    background-color: #ffcccc;\n" + 
    "}\n" + 
    "\n" + 
    ".breakpoint {\n" + 
    "    background-color: #cccccc;\n" + 
    "    border: 1px solid black;\n" + 
    "}\n" +
    "</style><title>Test suite results</title></head>\n" + 
    "<body>\n<h1>Test suite results </h1>";
    private static final String SUMMARY_HTML =  
            "\n\n<table>\n<tr>\n<td>result:</td>\n<td>{0}</td>\n" +
            "</tr>\n<tr>\n<td>totalTime:</td>\n<td>{1}</td>\n</tr>\n" +
            "<tr>\n<td>numTestPasses:</td>\n<td>{2}</td>\n</tr>\n" +
            "<tr>\n<td>numTestFailures:</td>\n<td>{3}</td>\n</tr>\n" +
            "<tr>\n<td>numCommandPasses:</td>\n<td>{4}</td>\n</tr>\n" +
            "<tr>\n<td>numCommandFailures:</td>\n<td>{5}</td>\n</tr>\n" +
            "<tr>\n<td>numCommandErrors:</td>\n<td>{6}</td>\n</tr>\n" +
            "<tr>\n<td>{7}</td>\n<td>&nbsp;</td>\n</tr>\n</table>";
    
    private static final String SUITE_HTML = "<tr>\n<td><a name=\"testresult{0}\">{1}</a><br/>{2}</td>\n<td>&nbsp;</td>\n</tr>";
    
    private final List<String> testTables;
    
    public HTMLTestResults(String postedResult, String postedTotalTime, 
            String postedNumTestPasses, String postedNumTestFailures, 
            String postedNumCommandPasses, String postedNumCommandFailures, 
            String postedNumCommandErrors, String postedSuite, List<String> postedTestTables) {

        result = postedResult;
        numCommandFailures = postedNumCommandFailures;
        numCommandErrors = postedNumCommandErrors;
        suite = new HTMLSuiteResult(postedSuite);
        totalTime = postedTotalTime;
        numTestPasses = postedNumTestPasses;
        numTestFailures = postedNumTestFailures;
        numCommandPasses = postedNumCommandPasses;
        testTables = postedTestTables;
    }


    public String getResult() {
        return result;
    }
    public String getNumCommandErrors() {
        return numCommandErrors;
    }
    public String getNumCommandFailures() {
        return numCommandFailures;
    }
    public String getNumCommandPasses() {
        return numCommandPasses;
    }
    public String getNumTestFailures() {
        return numTestFailures;
    }
    public String getNumTestPasses() {
        return numTestPasses;
    }
    public Collection getTestTables() {
        return testTables;
    }
    public String getTotalTime() {
        return totalTime;
    }
    public int getNumTotalTests() {
        return Integer.parseInt(numTestPasses) + Integer.parseInt(numTestFailures);
    }

    public void write(Writer out) throws IOException {
        out.write(HEADER);
        out.write(MessageFormat.format(SUMMARY_HTML,
                result,
                totalTime,
                numTestPasses,
                numTestFailures,
                numCommandPasses,
                numCommandFailures,
                numCommandErrors,
                suite.getUpdatedSuite()));
        for (int i = 0; i < testTables.size(); i++) {
            String table = testTables.get(i);
            out.write(MessageFormat.format(SUITE_HTML, i, suite.getHref(i), table));
        }
        out.write("</table></body></html>");
        out.flush();
    }
    
    class UrlDecoder {

        public String decode(String string) {
            try {
                return URLDecoder.decode(string, System.getProperty("file.encoding"));
            } catch (UnsupportedEncodingException e) {
                return string;
            }
        }
        
        public List decodeListOfStrings(List list) {
            List<String> decodedList = new LinkedList<String>();
            
            for (Iterator i = list.iterator(); i.hasNext();) {
                decodedList.add(decode((String) i.next()));
            }
            
            return decodedList;
        }
    }
}
