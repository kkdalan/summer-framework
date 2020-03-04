package com.brbear.summer.framework.orm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Page<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_PAGE_SIZE = 20;

	private int pageSize = DEFAULT_PAGE_SIZE;
	private int start;
	private int totalSize;
	private List<T> rows;

	public Page() {
		this(0, 0, DEFAULT_PAGE_SIZE, new ArrayList<T>());
	}

	public Page(int start, int pageSize, int totalSize, List<T> rows) {
		this.start = start;
		this.pageSize = pageSize;
		this.totalSize = totalSize;
		this.rows = rows;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public int getTotalPageCount() {
		if (totalSize % pageSize == 0) {
			return totalSize / pageSize;
		} else {
			return totalSize / pageSize + 1;
		}
	}

	public int getPageSize() {
		return pageSize;
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	public int getPageNo() {
		return start / pageSize + 1;
	}

	public boolean hasNextPage() {
		return getPageNo() < getTotalPageCount();
	}

	public boolean hasPreviousPage() {
		return getPageNo() > 1;
	}

	protected static int getStartOfPage(int pageNo) {
		return getStartOfPage(pageNo, DEFAULT_PAGE_SIZE);
	}

	public static int getStartOfPage(int pageNo, int pageSize) {
		return (pageNo - 1) * pageSize;
	}

}
