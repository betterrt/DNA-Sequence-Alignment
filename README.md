# DNA-Sequence-Alignment

## Objective

The program will find the similarity between two DNA sequences, which is described by the cost of the alignment. What's more, the program can also find the alignments of two DNA sequences, the time used and the memory used.

## Definition of Alignment

Suppose we are given two strings 𝑋 and 𝑌, where 𝑋 consists of the sequence of symbols 𝑥1, 𝑥2 , ... , 𝑥𝑚 and 𝑌 consists of the sequence of symbols 𝑦1, 𝑦2 , ... , 𝑦𝑛.
Consider the sets {1, 2, ... , 𝑚} and {1, 2, ... , 𝑛} as representing the different positions in the strings 𝑋 and 𝑌, and consider a matching of these sets. We say that a matching 𝑀 of these two sets is an alignment if there are no “crossing” pairs:if (𝑖, 𝑗), (𝑖', 𝑗') ϵ 𝑀 and 𝑖 < 𝑖', then 𝑗 < 𝑗'. Intuitively, an alignment gives a way of lining up the two strings, by telling us which pairs of positions will be lined up with one another.

## Definition of Similarity

The definition of similarity will be based on finding the optimal alignment between two DNA sequences 𝑋 and 𝑌, according to the following criteria. Suppose 𝑀 is a given alignment between 𝑋 and 𝑌:
1. First, there is a parameter δ > 0 that defines a gap penalty. For each position of 𝑋 or 𝑌 that is not matched in 𝑀( i.e. it is a gap), we incur a cost of δ.
2. Second, for each pair of letters 𝑝, 𝑞 in our alphabet, there is a mismatch cost of α𝑝𝑞 for lining up 𝑝 with 𝑞. Thus, for each (𝑖, 𝑗) ϵ 𝑀, we pay the appropriate mismatch cost α𝑥𝑖𝑦𝑗 for lining up 𝑥𝑖 with 𝑦𝑗. assumes that = 0 for each letter —there is no mismatch cost to line up α𝑝𝑝 𝑝 a letter with another copy of itself—although this will not be necessary in anything that follows.
3. The cost of 𝑀 is the sum of its gap and mismatch costs, and we seek an alignment of minimum cost, which descirbe the similaity of these two DNA sequences.

Example: Say we have first DNA sequence "AGCT" and second DNA sequence "AGT". The best aligment will be "AGCT" and "AG_T" where "_" means a gap.

## Basic and Efficient Method

The basic method is to use DP, which have a quadratic time complexity and space complexity.

The efficient method is to combine DP and D&C, which has also a quadratic time complexoty but a linear space complexity.
