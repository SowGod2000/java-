package com.firsthotel.member.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.firsthotel.member.bean.OperationLog;
import com.firsthotel.member.bean.OperationLogRepository;

@Service
public class logService {
	
	@Autowired
	private OperationLogRepository operationLogRepository;
	
	public String generateLogsCsv() 
	{
		List<OperationLog> logs = operationLogRepository.findAll();
		StringBuilder sb=new StringBuilder();
		
		sb.append("操作時間,操作者,動作內容\r\n");
		
		for(OperationLog log : logs) 
		{
			
			sb.append(log.getMember().getEmail()).append(",");
			sb.append(log.getAction()).append(",");
			sb.append(log.getCreatedAt()).append(",");
			sb.append(log.getDescription()).append("\r\n");
		}
		return sb.toString();
	}

}
