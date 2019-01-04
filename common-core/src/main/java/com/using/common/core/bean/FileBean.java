package com.using.common.core.bean;

import java.io.IOException;
import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

public class FileBean implements Serializable {
	private static final long serialVersionUID = 1L;
	protected String fileName;
	protected byte[] bytes;
	protected String contentType;
	protected long size;

	public FileBean() {

	}

	public FileBean(MultipartFile file) throws IOException {
		this.setFileName(file.getOriginalFilename());
		this.setBytes(file.getBytes());
		this.setContentType(file.getContentType());
		this.setSize(file.getSize());
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
}
