package com.hazelcast.nio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tomasz Nurkiewicz
 * @since 25.09.12, 12:03
 */
@RunWith(com.hazelcast.util.RandomBlockJUnit4ClassRunner.class)
public class IOUtilTest
{

    private static final byte[] NON_EMPTY_BYTE_ARRAY = new byte[100];
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private static final int SIZE = 3;

    @Test
    public void shouldReturnMinusOneWhenEmptyByteBufferProvidedAndReadingOneByte() throws Exception
    {
        //given
        final ByteBuffer empty = ByteBuffer.wrap(EMPTY_BYTE_ARRAY);
        final InputStream inputStream = IOUtil.newInputStream(empty);

        //when
        final int read = inputStream.read();

        //then
        assertEquals(-1, read);
    }

    @Test
    public void shouldReadWholeByteBuffer() throws Exception
    {
        //given
        final ByteBuffer empty = ByteBuffer.wrap(new byte[SIZE]);
        final InputStream inputStream = IOUtil.newInputStream(empty);

        //when
        final int read = inputStream.read(new byte[SIZE]);

        //then
        assertEquals(3, read);
    }

    @Test
    public void shouldAllowReadingByteBufferInChunks() throws Exception
    {
        //given
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[SIZE]);
        final InputStream inputStream = IOUtil.newInputStream(buffer);

        //when
        final int firstRead = inputStream.read(new byte[1]);
        final int secondRead = inputStream.read(new byte[SIZE - 1]);

        //then
        assertEquals(1, firstRead);
        assertEquals(SIZE - 1, secondRead);
    }

    @Test
    public void shouldReturnMinusOneWhenNothingRemainingInByteBuffer() throws Exception
    {
        //given
        final int SIZE = 3;
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[SIZE]);
        final InputStream inputStream = IOUtil.newInputStream(buffer);
        inputStream.read(new byte[SIZE]);

        //when
        final int read = inputStream.read();

        //then
        assertEquals(-1, read);
    }

    @Test
    public void shouldReturnMinusOneWhenEmptyByteBufferProvidedAndReadingSeveralBytes() throws Exception
    {
        //given
        final ByteBuffer empty = ByteBuffer.wrap(EMPTY_BYTE_ARRAY);
        final InputStream inputStream = IOUtil.newInputStream(empty);

        //when
        final int read = inputStream.read(NON_EMPTY_BYTE_ARRAY);

        //then
        assertEquals(-1, read);
    }

    @Test
    public void shouldThrowWhenTryingToReadFullyFromEmptyByteBuffer() throws Exception
    {
        //given
        final ByteBuffer empty = ByteBuffer.wrap(EMPTY_BYTE_ARRAY);
        final DataInputStream inputStream = new DataInputStream(IOUtil.newInputStream(empty));

        try
        {
            //when
            inputStream.readFully(NON_EMPTY_BYTE_ARRAY);
            fail("EOFException expected");
        }
        //then
        catch (EOFException e)
        {
        }
    }

    @Test
    public void shouldThrowWhenByteBufferExhaustedAndTryingToReadFully() throws Exception
    {
        //given
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[SIZE]);
        final DataInputStream inputStream = new DataInputStream(IOUtil.newInputStream(buffer));
        inputStream.readFully(new byte[SIZE]);

        try
        {
            //when
            inputStream.readFully(NON_EMPTY_BYTE_ARRAY);
            fail("EOFException expected");
        }
        //then
        catch (EOFException e)
        {
        }
    }


}
