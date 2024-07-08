import java.util.*;
import java.util.stream.Collectors;

public class TextSummarization {

    // Dummy stop words list
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList("a", "an", "the", "and", "is", "in", "at", "of", "to", "with", "that", "for", "on", "by", "this", "it", "as", "from", "or", "which", "but", "be", "has", "are", "was", "were", "been", "their"));

    public static void main(String[] args) {
        String text = "Artificial intelligence (AI) refers to the simulation of human intelligence in machines that are programmed to think like humans and mimic their actions. "
                    + "The term may also be applied to any machine that exhibits traits associated with a human mind such as learning and problem-solving. "
                    + "The ideal characteristic of artificial intelligence is its ability to rationalize and take actions that have the best chance of achieving a specific goal. "
                    + "A subset of artificial intelligence is machine learning, which refers to the concept that computer programs can automatically learn from and adapt to new data without being assisted by humans. "
                    + "Deep learning techniques enable this automatic learning through the absorption of huge amounts of unstructured data such as text, images, or video.";

        List<String> sentences = Arrays.asList(text.split("\\. "));
        Map<String, Integer> wordFrequency = calculateWordFrequency(text);
        Map<String, Double> tfidfScores = calculateTfIdfScores(wordFrequency, sentences);

        SummaryResult summaryResult = generateSummary(text, sentences, tfidfScores, 3); // Generate summary with top 3 sentences
        System.out.println("Original text: " + summaryResult.getOriginalText());
        System.out.println("Number of words in original text: " + summaryResult.getNumWordsOriginalText());
        System.out.println("Summarized text: " + summaryResult.getSummarizedText());
        System.out.println("Number of words in summarized text: " + summaryResult.getNumWordsSummarizedText());
    }

    private static Map<String, Integer> calculateWordFrequency(String text) {
        Map<String, Integer> frequency = new HashMap<>();
        String[] words = text.toLowerCase().split("\\W+");
        for (String word : words) {
            if (!STOP_WORDS.contains(word)) {
                frequency.put(word, frequency.getOrDefault(word, 0) + 1);
            }
        }
        return frequency;
    }

    private static Map<String, Double> calculateTfIdfScores(Map<String, Integer> wordFrequency, List<String> sentences) {
        Map<String, Double> tfidfScores = new HashMap<>();
        for (String word : wordFrequency.keySet()) {
            double tf = wordFrequency.get(word);
            double idf = Math.log(sentences.size() / (1.0 + countWordInSentences(word, sentences)));
            tfidfScores.put(word, tf * idf);
        }
        return tfidfScores;
    }

    private static long countWordInSentences(String word, List<String> sentences) {
        return sentences.stream().filter(sentence -> sentence.contains(word)).count();
    }

    private static SummaryResult generateSummary(String originalText, List<String> sentences, Map<String, Double> tfidfScores, int summaryLength) {
        Map<String, Double> sentenceScores = new HashMap<>();
        for (String sentence : sentences) {
            double score = Arrays.stream(sentence.split("\\W+"))
                                 .mapToDouble(word -> tfidfScores.getOrDefault(word.toLowerCase(), 0.0))
                                 .sum();
            sentenceScores.put(sentence, score);
        }

        List<String> summary = sentenceScores.entrySet().stream()
                                             .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                                             .limit(summaryLength)
                                             .map(Map.Entry::getKey)
                                             .collect(Collectors.toList());

        String summarizedText = String.join(". ", summary) + ".";
        int numWordsOriginalText = originalText.split("\\W+").length;
        int numWordsSummarizedText = summarizedText.split("\\W+").length;

        return new SummaryResult(originalText, numWordsOriginalText, summarizedText, numWordsSummarizedText);
    }

    static class SummaryResult {
        private final String originalText;
        private final int numWordsOriginalText;
        private final String summarizedText;
        private final int numWordsSummarizedText;

        public SummaryResult(String originalText, int numWordsOriginalText, String summarizedText, int numWordsSummarizedText) {
            this.originalText = originalText;
            this.numWordsOriginalText = numWordsOriginalText;
            this.summarizedText = summarizedText;
            this.numWordsSummarizedText = numWordsSummarizedText;
        }

        public String getOriginalText() {
            return originalText;
        }

        public int getNumWordsOriginalText() {
            return numWordsOriginalText;
        }

        public String getSummarizedText() {
            return summarizedText;
        }

        public int getNumWordsSummarizedText() {
            return numWordsSummarizedText;
        }
    }
}