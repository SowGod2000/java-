package com.firsthotel.member.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Members")
	public class Member {
	
		@Id @Column(name = "MEMBERID")
		@GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int memberID;
		@Column(name = "NAME")
		
	    private String name;
		
		@Column(name = "EMAIL")
		
	    private String email;
		@Column(name = "PHONE")
		
	    private String phone;
		
		@Column(name = "BIRTHDATE")
		@JsonFormat(pattern = "yyyy-MM-dd")
	    private LocalDate  birthDate;
		@Column(name = "ADDRESS")
		
	    private String address;
		@Column(name = "IDCARD")
		
	    private String idCard;
		@Column(name = "PASSWORD")
		
	    private String password;
		@Column(name = "JOINDATE")
		
	    private Date joinDate;
		@Column(name = "ISDELETED")
	    private int isDeleted;
		
		@Column(name = "DELETEDAT")
	    private Date deletedAt;
		
		@Column(name = "ROLE") 
	    private String role;
		
		private String token;
		
		private String resetToken;
		
		private LocalDateTime resetTokenExpiration;
		
		private String otp;
		
		 @JsonIgnore
		 @OneToMany(mappedBy = "member")
		 private List<OperationLog> operationLogs;
		
		
		
		public Member(String token) 
		{
			this.token=token;
		}
		
		public Member(String token,Member member) {
			this.name = member.name;
			this.email = member.email;
			this.phone = member.phone;
			this.birthDate = member.birthDate;
			this.address = member.address;
			this.idCard = member.idCard;
			this.token = token;
		}

	}
	