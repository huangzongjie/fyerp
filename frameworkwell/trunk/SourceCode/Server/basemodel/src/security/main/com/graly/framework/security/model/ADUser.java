package com.graly.framework.security.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import javax.persistence.ManyToMany;
import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="AD_USER")
public class ADUser extends ADUpdatable {

	private static final long serialVersionUID = 1L;
	public static final String SUPER_ADMIN ="SuperAdmin";
	
	@Column(name="USER_NAME")
	private String userName;
	
	@Column(name="DESCRIPTION")
	private String description;

	@Column(name="PASSWORD")
	private String password;
	
	@Column(name="IS_INVALID")
	private String isInValid;
	
	@Column(name="EMAIL")
	private String email;
	
	@Column(name="PHONE")
	private String phone;
	
	@Column(name="PHONE2")
	private String phone2;
	
	@Column(name="DEPARTMENT")
	private String department;
	
	@Column(name="BIRTH_DAY")
	private Date birthDay;
	
	@Column(name="SEX")
	private String sex;
	
	@Column(name="JOIN_TIME")
	private Date joinTime;
	
	@Column(name="DEFAULT_LANGUAGE")
	private String defLanguage;
	
	@Column(name="COMMENTS")
	private String comments;
	
	@Column(name="PWD_CHANGED")
	private Date pwdChanged;
	
	@Column(name="PWD_LIFE")
	private Long pwdLife;
	
	@Column(name="PWD_EXPIRY")
	private Date pwdExpiry;
	
	@Column(name="LAST_LOGON")
	private Date lastLogon;
	
	@Column(name="MENU_START")
	private String menuStart;
	
	@Column(name="DEFAULT_ORG_RRN")
	private Long defaultOrgRrn;
	
	@Column(name="IS_SHOW_LAUNCHER")
	private String isShowLauncher;
	
	@Column(name="DEFAULT_VIEW")
	private String defaultView;
	
	@Column(name="IS_PURCHASER")
	private String isPurchaser;
	
	@ManyToMany(targetEntity = ADUserGroup.class, mappedBy = "users")
	private List<ADUserGroup> userGroupsInv;
	
	@ManyToMany(targetEntity = ADUserGroup.class, fetch=FetchType.LAZY)
	@JoinTable(name = "AD_USERGROUP_USER",
			inverseJoinColumns = @JoinColumn(name = "USERGROUP_RRN", referencedColumnName = "OBJECT_RRN"),
			joinColumns = @JoinColumn(name = "USER_RRN", referencedColumnName = "OBJECT_RRN"))
	private List<ADUserGroup> userGroups;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable( name ="AD_USER_ORG",
            joinColumns = {@JoinColumn(name = "USER_RRN", referencedColumnName = "OBJECT_RRN")},
            inverseJoinColumns = {@JoinColumn(name = "ORG_RRN", referencedColumnName = "OBJECT_RRN")})
	private List<ADOrg> orgs;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="AD_USER_WORKCENTER",
			inverseJoinColumns={@JoinColumn(name="WORKCENTER_RRN", referencedColumnName="OBJECT_RRN")},
			joinColumns={@JoinColumn(name="USER_RRN", referencedColumnName="OBJECT_RRN")})
	private List<WorkCenter> workCenters;
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getUserName() {
		return userName;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setIsInValid(Boolean isInValid) {
		this.isInValid = isInValid ? "Y" : "N";
	}
	
	public Boolean getIsInValid(){
		return "Y".equalsIgnoreCase(this.isInValid) ? true : false; 
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public String getPhone2() {
		return phone2;
	}
	
	public void setDepartment(String department) {
		this.department = department;
	}
	
	public String getDepartment() {
		return department;
	}

	public void setBirthDay(Date birthy) {
		this.birthDay = birthy;
	}

	public Date getBirthDay() {
		return birthDay;
	}
	
	public void setSex(String sex) {
		this.sex = sex;
	}
	
	public String getSex() {
		return sex;
	}
	
	public void setJoinTime(Date joinTime) {
		this.joinTime = joinTime;
	}
	
	public Date getJoinTime() {
		return joinTime;
	}
	
	public void setDefLanguage(String defLanguage) {
		this.defLanguage = defLanguage;
	}
	
	public String getDefLanguage() {
		return defLanguage;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getComments() {
		return comments;
	}
	
	public void setPwdChanged(Date pwdChanged) {
		this.pwdChanged = pwdChanged;
	}
	
	public Date getPwdChanged() {
		return pwdChanged;
	}
	
	public void setPwdLife(Long pwdLife) {
		this.pwdLife = pwdLife;
	}
	
	public Long getPwdLife() {
		return pwdLife;
	}
	
	public void setPwdExpiry(Date pwdExpiry) {
		this.pwdExpiry = pwdExpiry;
	}
	
	public Date getPwdExpiry() {
		return pwdExpiry;
	}
	
	public void setLastLogon(Date lastLogon) {
		this.lastLogon = lastLogon;
	}
	
	public Date getLastLogon() {
		return lastLogon;
	}

	public void setMenuStart(String menuStart) {
		this.menuStart = menuStart;
	}
	
	public String getMenuStart() {
		return menuStart;
	}

	public void setDefaultOrgRrn(Long defaultOrgRrn) {
		this.defaultOrgRrn = defaultOrgRrn;
	}

	public Long getDefaultOrgRrn() {
		return defaultOrgRrn;
	}
	
	public void setIsShowLauncher(Boolean isShowLauncher) {
		this.isShowLauncher =isShowLauncher ? "Y" : "N";
	}
	
	public Boolean getIsShowLauncher(){
		return "Y".equalsIgnoreCase(this.isShowLauncher) ? true : false; 
	}
	
	public void setIsPurchaser(Boolean isPurchaser) {
		this.isPurchaser = isPurchaser ? "Y" : "N";
	}
	
	public Boolean getIsPurchaser(){
		return "Y".equalsIgnoreCase(this.isPurchaser) ? true : false; 
	}
	
	public String getDefaultView() {
		return defaultView;
	}

	public void setDefaultView(String defaultView) {
		this.defaultView = defaultView;
	}
	
	public void setUserGroups(List<ADUserGroup> userGroups) {
		this.userGroups = userGroups;
	}

	public List<ADUserGroup> getUserGroups() {
		return userGroups;
	}
	
	public void setOrgs(List<ADOrg> orgs) {
		this.orgs = orgs;
	}

	public List<ADOrg> getOrgs() {
		return orgs;
	}

	public List<ADUserGroup> getUserGroupsInv() {
		return userGroupsInv;
	}

	public List<WorkCenter> getWorkCenters() {
		return workCenters;
	}
}
