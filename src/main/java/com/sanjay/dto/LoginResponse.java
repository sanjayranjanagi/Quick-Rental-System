package com.sanjay.dto;


public class LoginResponse {
	 private String message;
	    private String token;
	    private Role role;
	    private Integer customerId;

	    public LoginResponse(String message, String token, Role role, Integer customerId) {
	        this.message = message;
	        this.token = token;
	        this.role = role;
	        this.customerId = customerId;
	    }

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}




		public Role getRole() {
			return role;
		}

		public void setRole(Role role) {
			this.role = role;
		}

		public Integer getCustomerId() {
			return customerId;
		}

		public void setCustomerId(Integer customerId) {
			this.customerId = customerId;
		}
	    
	    
}
