package nl.tudelft.rvh.test.chapter1;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Random;

import nl.tudelft.rvh.chapter1.Buffer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BufferTest {

	private Buffer buffer;
	@Mock private Random mockedRandom;

	@Before
	public void setUp() {
		this.buffer = new Buffer(5, 10);
	}

	@Test
	public void testWork() {
		when(this.mockedRandom.nextDouble()).thenReturn(0.5, 0.1);
		int queue = this.buffer.work(this.mockedRandom, 4);

		assertEquals(1, queue);
	}

	@Test
	public void testWork2Times() {
		when(this.mockedRandom.nextDouble()).thenReturn(0.5, 0.1, 0.6, 0.2);
		this.buffer.work(this.mockedRandom, 4);
		int queue = this.buffer.work(this.mockedRandom, 3);

		assertEquals(2, queue);
	}
}
