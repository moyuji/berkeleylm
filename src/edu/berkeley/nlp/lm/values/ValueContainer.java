package edu.berkeley.nlp.lm.values;

import edu.berkeley.nlp.lm.bits.BitList;
import edu.berkeley.nlp.lm.bits.BitStream;
import edu.berkeley.nlp.lm.map.NgramMap;
import edu.berkeley.nlp.lm.util.Annotations.OutputParameter;

/**
 * Manages storage of arbitrary values in an NgramMap
 * 
 * @author adampauls
 * 
 * @param <V>
 */
public interface ValueContainer<V>
{

	/**
	 * Adds a new value at the specified offset.
	 * 
	 * @param ngramOrder
	 *            As always, ngramOrder is 0-based (0=unigram)
	 * @param offset
	 * @param contextOffset
	 * @param word
	 * @param val
	 * @param suffixOffset
	 */
	public void add(int[] ngram, int startPos, int endPos, int ngramOrder, long offset, long contextOffset, int word, V val, long suffixOffset,
		boolean ngramIsNew);

	/**
	 * Swaps values at offsets a and b.
	 * 
	 * @param a
	 * @param b
	 * @param ngramOrder
	 */
	public void swap(long a, long b, int ngramOrder);

	/**
	 * Sets internal storage for size for a particular n-gram order
	 * 
	 * @param size
	 * @param ngramOrder
	 */
	public void setSizeAtLeast(long size, int ngramOrder);

	/**
	 * Creates a fresh value container for copying purposes.
	 * 
	 * @return
	 */
	public ValueContainer<V> createFreshValues();

	/**
	 * Gets the value living at a particular offset.
	 * 
	 * @param offset
	 * @param ngramOrder
	 * @return
	 */
	public void getFromOffset(long offset, int ngramOrder, @OutputParameter V outputVal);

	/**
	 * Destructively sets internal storage from another object.
	 * 
	 * @param other
	 */
	public void setFromOtherValues(ValueContainer<V> other);

	/**
	 * Compresses the value at the given offset into a list of bits.
	 * 
	 * @param offset
	 * @param ngramOrder
	 * @return
	 */
	public BitList getCompressed(long offset, int ngramOrder);

	/**
	 * Reads and decompresses from the bit stream bits.
	 * 
	 * @param bits
	 * @param ngramOrder
	 * @param justConsume
	 *            If true, nothing is returned, and the function simply consumes
	 *            the appropriate number of bits from the BitStream.
	 * 
	 * @return
	 */
	public void decompress(BitStream bits, int ngramOrder, boolean justConsume, @OutputParameter V outputVal);

	public void clearStorageAfterCompression(int ngramOrder);

	/**
	 * Clear storage after an n-gram order is complete
	 * 
	 * @param ngramOrder
	 * @param size
	 */
	public void trimAfterNgram(int ngramOrder, long size);

	/**
	 * Final clean up of storage.
	 */
	public void trim();

	/**
	 * Retrieves a stored suffix offset for a n-gram (given by offset)
	 * 
	 * @param offset
	 * @param ngramOrder
	 * @return
	 */
	public long getContextOffset(long offset, int ngramOrder);

	/**
	 * Creates a fresh value of object (useful for passing as an output
	 * parameter)
	 * 
	 * @return
	 */
	public V getScratchValue();

	/**
	 * Initializes a value container with the map that contains it
	 */
	public void initForMap(NgramMap<V> map);

}