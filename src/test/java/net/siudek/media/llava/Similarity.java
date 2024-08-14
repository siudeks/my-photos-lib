package net.siudek.media.llava;

import java.util.List;

/**
 * Used by tests to compare embeddings.
 * more:
 * https://stackoverflow.com/questions/520241/how-do-i-calculate-the-cosine-similarity-of-two-vectors
 */
public class Similarity {

  /**
   * Similarity of provided embedings.
   *
   * @return 0 - 1, where 0 are different, 1 are identical
   */
  public static double cosine(List<Double> vectorA, List<Double> vectorB) {
    return cosine(vectorA.stream().mapToDouble(it -> it).toArray(),
        vectorB.stream().mapToDouble(it -> it).toArray());
  }

  /**
   * Similarity of provided embedings.
   *
   * @return 0 - 1, where 0 are different, 1 are identical
   */
  public static double cosine(double[] vectorA, double[] vectorB) {
    double dotProduct = 0.0;
    double normA = 0.0;
    double normB = 0.0;
    for (int i = 0; i < vectorA.length; i++) {
        dotProduct += vectorA[i] * vectorB[i];
        normA += Math.pow(vectorA[i], 2);
        normB += Math.pow(vectorB[i], 2);
    }   
    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
  }

    /**
     * from  www.java2s.com
    * Calculate the cosine similarity between two vectors
    * 
    * @param a
    *            user a's ratings
    * @param b
    *            user b's ratings
    * @return cosine similarity
    */
   public static double cosine2(double[] a, double[] b) {
       if (a == null || b == null || a.length < 1 || b.length < 1 || a.length != b.length)
           return Double.NaN;

       double sum = 0.0, sum_a = 0, sum_b = 0;
       for (int i = 0; i < a.length; i++) {
           sum += a[i] * b[i];
           sum_a += a[i] * a[i];
           sum_b += b[i] * b[i];
       }

       double val = Math.sqrt(sum_a) * Math.sqrt(sum_b);

       return sum / val;
   }


   public static double cosine3(List<Double> vectorA, List<Double> vectorB) {
    return cosine3(vectorA.stream().mapToDouble(it -> it).toArray(),
        vectorB.stream().mapToDouble(it -> it).toArray());
  }

  static double cosine3(double[] vectorA, double[] vectorB) {
    double dotProduct = 0.0;
    double magnitudeA = 0.0;
    double magnitudeB = 0.0;
    
    // Check if both vectors are of the same length
    if (vectorA.length != vectorB.length) {
        throw new IllegalArgumentException("Vectors must be of same length");
    }
    
    // Calculate dot product and magnitudes
    for (int i = 0; i < vectorA.length; i++) {
        dotProduct += vectorA[i] * vectorB[i];
        magnitudeA += Math.pow(vectorA[i], 2);
        magnitudeB += Math.pow(vectorB[i], 2);
    }
    
    // Calculate magnitudes
    magnitudeA = Math.sqrt(magnitudeA);
    magnitudeB = Math.sqrt(magnitudeB);
    
    // Check if magnitudes are not zero to prevent division by zero
    if (magnitudeA == 0.0 || magnitudeB == 0.0) {
        return 0.0;
    }
    
    // Return cosine similarity
    return dotProduct / (magnitudeA * magnitudeB);
   }
   
}
