package com.onmobile.vol.referralchain.app.utils;

import java.io.Serializable;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;


public class OffsetBasedPageRequest implements Pageable, Serializable {

	private static final long serialVersionUID = -1501304200113082175L;
	
	private int size;
    private long offset;
    private final Sort sort ;
    
     
    private OffsetBasedPageRequest(long nextOffset, int size, Sort sort) {
        if (nextOffset < 0) {
            throw new IllegalArgumentException("Offset  must not be less than zero!");
        }

        if (size < 1) {
            throw new IllegalArgumentException("size must not be less than one!");
        }
        this.size = size;
        this.offset = nextOffset;
        this.sort = sort;
    }

    /**
     * Creates a new unsorted {@link OffsetBasedPageRequest}.
     *
     * @param offset zero-based offset.
     * @param size the size of the page to be returned.
     */
     public static OffsetBasedPageRequest of(long offset, int size) {
                     return of(offset, size, null);
     }

     /**
     * Creates a new {@link OffsetBasedPageRequest} with sort parameters applied.
     *
     * @param offset zero-based offset.
     * @param size the size of the page to be returned.
     * @param sort must not be {@literal null}.
     */
     public static OffsetBasedPageRequest of(long offset, int size, Sort sort) {
                     return new OffsetBasedPageRequest(offset, size, sort);
     }

     /**
     * Creates a new {@link OffsetBasedPageRequest} with sort direction and properties applied.
     *
     * @param offset zero-based offset.
     * @param size the size of the page to be returned.
     * @param direction must not be {@literal null}.
     * @param properties must not be {@literal null}.
     */
     public static OffsetBasedPageRequest of(int offset, int size, Direction direction, String... properties) {
                     return of(offset, size,  new Sort(direction, properties));
     }

	
	@Override
	public int getPageNumber() {
		return (int)offset/size;
	}

	@Override
	public int getPageSize() {
		return size;
	}

	@Override
	public long getOffset() {
		return offset;
	}

	@Override
	public Sort getSort() {
		return sort;
	}

	@Override
    public Pageable next() {
		long nextOffset = getOffset() + getPageSize();
        return new OffsetBasedPageRequest(nextOffset, getPageSize(), getSort());
    }

    public OffsetBasedPageRequest previous() {
    	long previousOffset = getOffset() - getPageSize();
        return hasPrevious() ? new OffsetBasedPageRequest(previousOffset, getPageSize(), getSort()) : this;
    }


    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    public Pageable first() {
        return new OffsetBasedPageRequest(0, getPageSize(), getSort());
    }

    @Override
    public boolean hasPrevious() {
        return offset > size;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (offset ^ (offset >>> 32));
		result = prime * result + size;
		result = prime * result + ((sort == null) ? 0 : sort.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OffsetBasedPageRequest other = (OffsetBasedPageRequest) obj;
		if (offset != other.offset)
			return false;
		if (size != other.size)
			return false;
		if (sort == null) {
			if (other.sort != null)
				return false;
		} else if (!sort.equals(other.sort))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OffsetBasedPageRequest [size=" + size + ", offset=" + offset + ", sort=" + sort + "]";
	}   
}