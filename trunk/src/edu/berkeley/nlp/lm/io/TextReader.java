package edu.berkeley.nlp.lm.io;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import edu.berkeley.nlp.lm.WordIndexer;
import edu.berkeley.nlp.lm.collections.Iterators;
import edu.berkeley.nlp.lm.util.Logger;

/**
 * Class for reading raw text files.
 * 
 * @author adampauls
 * 
 * @param <W>
 */
public class TextReader<W> implements LmReader<Object, LmReaderCallback<Object>>
{
	private final int lmOrder;

	private final WordIndexer<W> wordIndexer;

	private final List<File> inputFiles;

	public TextReader(final List<File> inputFiles, final WordIndexer<W> wordIndexer, final int maxOrder) {
		this.inputFiles = inputFiles;
		this.lmOrder = maxOrder;
		this.wordIndexer = wordIndexer;

	}

	/**
	 * Reads newline-separated plain text from inputFiles, and writes an ARPA lm
	 * file to outputFile. If files have a .gz suffix, then they will be
	 * (un)zipped as necessary.
	 * 
	 * @param inputFiles
	 * @param outputFile
	 */
	@Override
	public void parse(final LmReaderCallback<Object> callback) {
		readFromFiles(callback);
	}

	private void readFromFiles(final LmReaderCallback<Object> callback) {
		Logger.startTrack("Reading from files " + inputFiles);
		final Iterable<String> allLinesIterator = getLineIterator(inputFiles);

		countNgrams(allLinesIterator, callback);
		Logger.endTrack();

	}

	/**
	 * @param <W>
	 * @param wordIndexer
	 * @param maxOrder
	 * @param allLinesIterator
	 * @param callback
	 * @param ngrams
	 * @return
	 */
	private void countNgrams(final Iterable<String> allLinesIterator, final LmReaderCallback<Object> callback) {
		long numLines = 0;

		for (final String line : allLinesIterator) {
			if (numLines % 10000 == 0) Logger.logs("On line " + numLines);
			numLines++;
			final String[] words = line.split(" ");
			final int[] sent = new int[words.length + 2];
			sent[0] = wordIndexer.getOrAddIndex(wordIndexer.getStartSymbol());
			sent[sent.length - 1] = wordIndexer.getOrAddIndex(wordIndexer.getEndSymbol());
			for (int i = 0; i < words.length; ++i) {
				sent[i + 1] = wordIndexer.getOrAddIndexFromString(words[i]);
			}
			for (int ngramOrder = 0; ngramOrder < lmOrder; ++ngramOrder) {
				for (int i = 0; i < sent.length; ++i) {
					if (i - ngramOrder < 0) continue;
					callback.call(sent, i - ngramOrder, i + 1, null, line);
				}
			}
		}
		callback.cleanup();
	}

	/**
	 * @param files
	 * @return
	 */
	private Iterable<String> getLineIterator(final Iterable<File> files) {
		final Iterable<String> allLinesIterator = Iterators.flatten(new Iterators.Transform<File, Iterator<String>>(files.iterator())
		{

			@Override
			protected Iterator<String> transform(final File file) {
				try {
					return IOUtils.lineIterator(file.getPath());
				} catch (final IOException e) {
					throw new RuntimeException(e);

				}
			}
		});
		return allLinesIterator;
	}

}