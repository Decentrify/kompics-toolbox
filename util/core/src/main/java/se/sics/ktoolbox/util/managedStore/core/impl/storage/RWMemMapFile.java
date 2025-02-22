/*
 * Copyright (C) 2009 Swedish Institute of Computer Science (SICS) Copyright (C)
 * 2009 Royal Institute of Technology (KTH)
 *
 * GVoD is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package se.sics.ktoolbox.util.managedStore.core.impl.storage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Set;
import se.sics.kompics.util.Identifier;
import se.sics.ktoolbox.util.managedStore.core.Storage;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class RWMemMapFile implements Storage {

    private final long length;
    private final MappedByteBuffer mbb;

    RWMemMapFile(File file, long length) throws IOException {
        this.length = length;

        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        mbb = raf.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, length);
        raf.close();
    }
    
    @Override
    public void tearDown() {
    }
    
    @Override
    public synchronized byte[] read(Identifier readerId, long readPos, int readLength, Set<Integer> bufferBlocks) {
        if(readPos > Integer.MAX_VALUE || readLength > Integer.MAX_VALUE) {
            throw new RuntimeException("MemoryMappedFiles only allow integer size for read values");
        }
        if(readPos > length) {
            return null;
        }
        if(readPos + readLength > length) {
            throw new RuntimeException("not tested yet");
//            readLength = length - readPos;
        }
        return read((int)readPos, (int)readLength);
    }
    
    private synchronized byte[] read(int readPos, int readLength) {
        byte[] result = new byte[readLength];
        mbb.position(readPos);
        mbb.get(result, 0, result.length);
        return result;
    }
    
    @Override
    public synchronized int write(long writePos, byte[] bytes) {
        if(writePos > Integer.MAX_VALUE) {
             throw new RuntimeException("MemoryMappedFiles only allow integer size for read values");
        }
        if(writePos > length) {
            return 0;
        }
        return write((int)writePos, bytes);
    }
    
    public synchronized int write(int writePos, byte[] bytes) {
        mbb.position(writePos);
        int restFile = (int)(length - writePos);
        int writeBytes = (bytes.length < restFile ? bytes.length : restFile);
        mbb.put(bytes, 0, writeBytes);
        mbb.force();
        return writeBytes;
    }

    @Override
    public long length() {
        return length;
    }
}
