package fr.svivien.cgbenchmark.producerconsumer;

import fr.svivien.cgbenchmark.model.request.play.PlayResponse;
import fr.svivien.cgbenchmark.model.test.TestInput;

public interface PlayResultListener {

	void consume(TestInput test, PlayResponse response);
	
}
