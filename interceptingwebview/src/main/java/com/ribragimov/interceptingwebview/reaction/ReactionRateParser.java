package com.ribragimov.interceptingwebview.reaction;

/**
 * Created by ribragimov on 7/26/18.
 */
public class ReactionRateParser {

    private final int MAX_STEP_COUNT = 1000;

    private String html;

    /**
     * This method sets html content for futher parsign
     *
     * @param html html content
     */
    public void setHtml(String html) {
        this.html = html;
    }

    /**
     * This method parses html and returns rate based on review id
     * Note, that this method returns:
     *  -1 -> when html is not set
     *  -2 -> when review id is not found in html content
     *  -3 -> when step count is exceeded max amount of steps
     *
     * @param reviewId review id
     * @return rate
     */
    public int getRate(String reviewId) {
        if (html == null || html.isEmpty()) return -1;

        int idx = html.indexOf(reviewId);
        if (idx == -1) {
            return -2;
        }

        // increment idx with the length of review and add 1 to skip \"\ character
        idx = idx + reviewId.length() + 1;

        int closingBracketsLeft = 3;
        int stepCount = 0;
        boolean isInsideString = false;

        while (true) {
            if (html.length() - 1 < idx) return -4;

            if (html.charAt(idx) == '\"') { // invert isInsideString
                isInsideString = !isInsideString;
            } else if (html.charAt(idx) == ']') { // decrement closingBracketsLeft when \]\ char is not inside the string
                if (!isInsideString) {
                    closingBracketsLeft--;
                }
            } else if (closingBracketsLeft == 0 && Character.isDigit(html.charAt(idx))) { // we found rate, cool
                return html.charAt(idx) - '0';
            }

            if (stepCount == MAX_STEP_COUNT) { // exceeded max amount of steps
                return -3;
            }

            idx++;
            stepCount++;
        }
    }

}
