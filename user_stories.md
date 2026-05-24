# Electricity Management System - User Stories

## US001: Customer Registration – Web
**Description:** As a Customer, I should be able to self register into the system with my consumer number.

**Acceptance Criteria:**
1. Registration Form Fields  • The registration form should include the following fields:   • Consumer Number(used to uniquely identify the customer in the system, should accept only numbers, 13 digit number. Customer need to supply the same)   • Full Name (required, cannot be empty and should accept only characters, Maximum 50 characters)   • Address (required and should ensure that address length meets minimum character requirements)   • Email (required and should be validated for correct email format)   • Mobile Number (required and should be validated for correct phone number format)   • Customer Type: Residential/Commercial   • Electrical Section: Office/Region   • User ID (unique identifier for login; min length 5 and max length 20)   • Password (with complexity requirements such as minimum 8 characters, at least one uppercase letter, one lowercase letter, one number, and one special character)   • Confirm Password (must match the password entered above)  • Confirmation Message : Upon successful submission, display a confirmation message indicating that the registration was successful along with Customer Id (Randomly generated), Customer Name and Email. * Also encrypt the password while saving in the database using some industry accepted encryption mechanism. 2. Field Validations  • Unique Fields : Ensure that fields like User ID, Email, and Consumer Number are unique.  • Password Requirements: Enforce password strength requirements (minimum length and complexity).   • Confirm Password Match : Verify that the "Confirm Password" field matches the "Password" field, displaying an error if they do not.  • Mobile Number Validation : Check that the mobile number is in the correct format and contains only numbers. 3. Validation Messages   • Specific Error Messages : Display specific error messages for validation failures:   • "Please enter a valid Consumer Number."   • "User ID already exists. Please choose a different User ID."   • "Passwords do not match."   • "Mobile number is invalid."   • "Incorrect email format." * Use web concepts for necessary actions.

---

## US002: View Home Page – Web
**Description:** As a Customer, I should be able to login to the application to view the home page

**Acceptance Criteria:**
1. Welcome Message and User Information  • The page should display a personalized welcome message, including the customer’s name.  • A section should display the logged-in user’s profile information, such as Account Number and Current Billing Address. 2. Navigation to Key Features  • The home page should have clear navigation options for key features, such as:  • View Bills  • Pay Bill  • Bill History  • Register Complaint  • Complaint Status  • Each feature should be represented by a clear icon or button and link to the respective pages. 3. Current Bill Summary  • A summary section should display details of the latest bill, including:  • Billing Period  • Due Date  • Amount Due  • If the bill is unpaid, a "Pay Now" button should be visible and lead to the payment page. 4. Profile and Account Settings  • A "Profile" or "Account Settings" link should be available in the top navigation or sidebar, allowing customers to view and update their profile information (e.g., contact details, billing address). 5. Logout Option  • A "Logout" button should be prominently displayed, allowing the user to securely log out.  • On clicking "Logout", the session should end, and the user should be redirected to the login p* Use web concepts for necessary actions.

---

## US003: View Bills – Web
**Description:** As a Customer, I should be able to login to the application to view all the electricity bills

**Acceptance Criteria:**
The customer should able to view the bills based on Bill Number on clicking the View/Pay Bill and should display the below details in a tabular format.    1)consumer No    2)Bill Number    3)Payment Status(Paid/Unpaid)    4)Connection Type(Domestic/Commercial)    5)Connection Status(Connected/Disconnected)    6)Mobile Number    7)Bill Period    8)Bill Date    9)Due Date    10)Disconnection Date    11)Due amount    12)Payable amount(text box to enter the amount) - There should be a check box against each bill record and customer should have the provision to choose the bills to be paid. - It should Calculate the total amount payable and display it on the screen (if a bill is not selected, total amount should be updated accordingly ) - On clicking the Proceed to Pay button, it should be redirected to Payment screen. * Use web concepts for necessary actions.D4

---

## US004: View Bill Summary – Web
**Description:** As a Customer, I should be able to view the selected bills

**Acceptance Criteria:**
1. The page should display a summary table listing each selected bill with the following fields:     • Consumer Number     • Bill Date     • Billing Period     • Bill Amount     • Due Date     • Each bill entry should have a checkbox or icon indicating it was selected for payment.     • A total amount section should display the cumulative amount for all selected bills. 2. Confirmation of Bill Selection     • The page should allow the user to review and confirm the bills they have selected.     • The user should be able to deselect any bill they no longer wish to pay from the summary page.     • A total amount section should display the cumulative amount for all selected bills.     • Updating the selection should dynamically update the total amount. 3. Payment Options     • The page should provide options for selecting the payment method (e.g., credit card, debit card, net banking).   4.Proceed to Payment Button      • A prominent “Proceed to Payment” button should be available, enabling users to confirm their bill selection and proceed to payment.      • The button should remain disabled if no bills are selected.      • Back button to enable the user to go back      • If the page encounters an error (e.g., bill details fail to load), a user-friendly error message should display, allowing the user to retry or go back. * Use web concepts for necessary actions.

---

## US005: Pay Bill – Web
**Description:** As a Customer, I should be able to pay the electricity bills

**Acceptance Criteria:**
1. Card Details Section  • The page should have fields to enter Card Number, Expiry Date, CVV, and Cardholder Name.  • Only valid card numbers of 16 digits should be accepted.  • The CVV field should only accept 3 digits (or 4 for some cards like American Express). 2. Validation Checks  • Card number, expiry date, and CVV should have front-end validation to prevent incorrect formats.  • Expiry date should ensure the card is not expired.  • All fields are required; submitting the form with missing fields should show appropriate error messages.       • If the payment fails (e.g. incorrect CVV, expired card), an error message should be displayed.  • The error message should be specific. 3. Validate the Payment Amount Display  • The total amount due should be displayed clearly and prominently on the page before the customer submits the payment. 4. Confirmation Prompt  • Once the user submits payment, a confirmation prompt should appear to review details before final submission.  • The user should be able to cancel or confirm the payment. 5. Successful Payment  • Upon successful payment, a success message along with the following details needs to be displayed:      -Payment id      -Transaction id      -Receipt Number      -Transaction Date      -Transaction Type(Credit/Debit)      -Bill Number      -Transaction Amount      -Transaction status  •There should be an option to download the receipt and invoice of the payment with all necessary details.  • The confirmation page should include a link to return to the account dashboard or e-bill history. - Use web concepts for the necessary actions.

---

## US006: Generate Invoice - Web
**Description:** As a Customer, I should be able to generate invoice for the payment

**Acceptance Criteria:**
Once the Payment is done by consumer, he should be able to view the invoice based on the Transaction id and display the below details.      -Invoice Number      -Payment id      -Transaction id      -Receipt Number      -Consumer details      -Transaction Date      -Transaction Type(Credit/Debit)      -Bill Number      -Transaction Amount      -Transaction status

---

## US007: Bill History – Web
**Description:** As a customer, I should be able to view my electricity usage history for a specified period.

**Acceptance Criteria:**
As a Customer, I should be able to view their electricity usage history with below details. 1. Bill Date 2. Billing Period 3. Due Date 4. Bill Amount 5. Payment Status 6. Payment Date 7. Mode of Payment 8. Download link or button to view full bill as a pdf or in another format. - The page should display all bills generated for the past six months by default with an option to adjust the date range - User should be able to sort the records by Bill Date, Due date or Bill Amount - User should be able to filter bills based on payment status(eg: view only unpaid bills) - Each bill record should have a link to view the detailed bill or download as pdf - All bill amounts and dates should be fetched and displayed accurately - If there is no billing data for the selected range, specific error message should be displayed. - Only authenticated customer should be able to view the bill history of a specific customer. - Use web concepts for the necessary actions and database if necessary.

---

## US008: Register Complaint/Service – Web
**Description:** As a Customer, I should be able to register complaint in the system.

**Acceptance Criteria:**
1. Complaint Registration Form  • The page should display a form with required fields:  • Complaint Type: Dropdown or radio buttons for selecting the type of complaint (e.g., billing issue, power outage, meter reading issue).  • Category : Drop down and based on the Complaint type selected, respective category should be displayed  • Description: A text area where the user can provide additional details about the complaint.  • Preferred Contact Method: Options for email or phone.  • Contact Details: Automatically populated with the registered contact information but editable if needed.  • All fields, including the complaint type,category and description, should be required for submission.  • Submit button: On clicking will submit the complaint.  • Reset Button: On clicking, it should reset and refresh the page. 2. Validation of Inputs  • The form should have validation to ensure that required fields are filled and meet expected formats (e.g., character limit on the description, valid email format).  • If validation fails, user-friendly error messages should display, indicating how to correct the input. 3. Submission Confirmation  • Upon successfully submitting a complaint, a confirmation message should display with:  • A unique Complaint ID for tracking.  • An estimated resolution time based on the complaint type.  • A summary of the complaint details.  • The confirmation message should give the option to return to the dashboard or view complaint status. - Use web concepts for the necessary actions.

---

## US009: Complaint Status – Web
**Description:** As a Customer, I should be able to check the status of complaint registered

**Acceptance Criteria:**
1. Track Complaint Status  • After submitting, users should be able to track the status of their complaint through the Complaint Status page.  • User should be able to track complaint either using the complaint id or status  • Complaint Number : Text box to enter the complaint id  • Status options might include Pending, In Progress, Resolved, and Closed.  • Submit button: On Clicking will display the details of complaint  • Each complaint should display the Complaint ID, submission date, type, and status. 2. Use web concepts for the necessary actions.

---

## US010: View Complaint History - Web
**Description:** As a customer , I should be able to view Complaint history

**Acceptance Criteria:**
1. View Complaint History  • Users should be able to view a history of all previously registered complaints, with fields for Complaint ID, type, date, and status.  • Each complaint should have detailed information, including the description and any notes. - Use web concepts for the necessary actions.

---

## US011: Add New Customer -Web
**Description:** As an admin, I should be able to add new customer

**Acceptance Criteria:**
1. Add New Customer  • Form Fields: The "Add Customer" form should include the following required fields:   • Full Name   • Address   • Consumer Number   • Contact Information (phone number and email)   • Customer Type (e.g., residential, commercial)   • User ID: Textbox min 5 and max 20     • Password: A default password should be assigned to the user and the customer should be able to login using this default password. System   should also prompt to change the password when customer tries to login for first time.    • The system should validate required fields and ensure uniqueness for fields like email and Consumer Number.  • If there are any validation errors, the system should display specific error messages (e.g., "Email already exists" or "Invalid contact number").  * Also encrypt the password while saving in the database using some industry accepted encryption mechanism. 2.Assign Consumer Number  • After successfully adding a new customer, a confirmation message should be displayed with Customer Id (Unique and random generated), Customer Name  and Email. The admin should be prompted to add at least one Consumer No associated with the customer, or they should be able to navigate to the "Add  New Consumer" page to link multiple Consumer Nos.  • A message should guide the admin stating, "Customer added successfully. Please add one or more Consumer Nos to complete the setup."

---

## US012: Add New Consumer- Web
**Description:** As an admin, I should be able to add/link new consumer number to a customer.

**Acceptance Criteria:**
1.Search Existing Customer  • Admins should search for an existing customer by Customer ID or Name to whom the new Consumer No will be linked.  • If the customer does not exist, the system should display a message like "Customer not found. Please add the customer first." 2. Form Fields for Consumer Details  • The "Add New Consumer" form should include the following fields:   • Consumer No (unique identifier)   • Address (address specific to this consumer No)   • Contact Information (phone number and email)   • Customer Type (e.g., residential, commercial)   • Validation : Ensure that the Consumer No is unique and that all required fields are correctly filled in.  • Upon successfully adding a consumer, a confirmation message should indicate that the Consumer No is now linked to the customer.  • If a Consumer No already exists or there are other validation errors, the system should display specific error messages to guide user. 3. Link Multiple Consumer Nos to One Customer  • The system should allow the admin to add multiple Consumer Nos to the same customer, either consecutively or at a later time.  • After adding, a list of all Consumer Nos associated with the customer should be displayed for confirmation.

---

## US013: List Consumers- Web
**Description:** As an admin, I should be able to view the consumer list.

**Acceptance Criteria:**
• Admin should be able to view all consumer list.   • The system should allow the admin to filter the list based on Electrical Section  • The system should allow the admin to filter the list based on Customer Type  • Proper pagination also need to be provided for consumer Listing.

---

## US014: Update Existing Consumer details-Web
**Description:** As an admin , I should be able to update the details of an existing Consumer

**Acceptance Criteria:**
1. Update Existing Consumer details  • Search for Customer: Admin should be able to search for a customer by Customer ID, Consumer Number, or name to retrieve their details.  • The search results should be displayed with Edit link or button against each record.  • On clicking the Edit, the following fields should be editable in the "Update Consumer" form:   • Full Name   • Address   • Contact Information   • Customer Type   • Non-editable Fields: Fields like Customer ID and Consumer Number should be view-only and cannot be changed.   • Update and Back button should be available in the form  • Ensure that the updated contact information, email, and other details are validated.  • After successfully updating customer information, a confirmation message should appear, indicating that the customer details were updated.  • If there is an error during the update, an appropriate error message should be displayed.

---

## US015: Disconnect/ReConnect Consumer- Web
**Description:** As an admin , I should be able to disconnect or reconnect an existing Consumer

**Acceptance Criteria:**
1. Disconnect/Reconnect Consumer Details  • The admin should be able to search for a customer by Customer ID or Consumer Number.  • The search results should be displayed with Disconnect and Reconnect radio button against each record.  • On clicking the Disconnect and Reconnect radio button, it should ask for the confirmation from user.  • Confirmation Prompt: The system should prompt the admin to confirm the action on clicking the radio buttons (e.g., "Are you sure you want to  Disconnect/Re Connect this customer?).  • Update the status of the customer as "Inactive" or "Active" on updation.  • Confirmation Message: After successful removal, a confirmation message should indicate that the customer is deactivated/activated.

---

## US016: Add Bill - Admin
**Description:** As an admin I should be able to add bills for a customer using the consumer No

**Acceptance Criteria:**
1.Add Bill Form  • The page should display a form with the following required fields:  •Consumer No: Input field to enter or select the consumer no.  •Billing Period: Input field or dropdown for the billing month/year (e.g., June 2024).  •Bill Date: Date picker to select the date when the bill was generated.  •Due Date: Date picker to select the payment due date.  •Disconnection date: Date picker to select the disconnection date.  •Bill Amount: Numeric input for entering the total bill amount.  •Late Fee (Optional): Numeric input for any late fees, set to 0 by default.  •Status: Dropdown to select the bill status (e.g., Unpaid, Paid).  • All required fields should be validated to ensure they are not empty and are in the correct format.         • If any field validation fails, an error message should display, clearly indicating the required corrections. 2.Validation of Inputs  • The Consumer No field should validate that the No exists in the system and should display an error message if it’s invalid.  • Dates should be validated to ensure that the Bill Date is before the Due Date.  • Bill Amount should only accept positive numeric values, and if the Late Fee is included, it must also be a positive number. 3.Save Bill  • Upon clicking "Save" the bill details should be saved to the database.  • After saving, a confirmation message should appear, displaying the details of the new bill confirming that it has been successfully added and a  bill id should be generated.  • The admin should have an option to add another bill or return to the dashboard. 4.Duplicate Bill Prevention  • If a bill for the same Consumer No and Billing Period already exists, an error message should inform the admin, preventing duplicate entries. 5. Bulk Bill Upload Option  • There should be a feature to bulk upload bills to the selected consumers. 6.Back to Dashboard Option  • The admin should have a "Cancel" or "Back to Dashboard" button to return to the main dashboard without saving the new bill.

---

## US017: View Bill- Admin
**Description:** As an admin I should be able to view bill history for a customer.

**Acceptance Criteria:**
1. Search Customer  • The admin should be able to enter a Customer ID or Consumer Number to view the bill history for a specific customer.  • The system should validate the entered ID or number to ensure it exists before fetching the history.  • If the customer ID or consumer number is invalid, an error message should be displayed. 2. Display Bill History  • If the entered id is valid, the system should display the customer’s bill history in a tabular format with the following details:   • Bill ID   • Billing Period (e.g., month and year)   • Bill Date   • Due Date   • Bill Amount   • Late Fee (if any)   • Payment Status (Paid or Unpaid)   • Payment Date (if paid)  • The bills should be sorted by Billing Period, with the most recent bill at the top. 3. Filtering and Sorting Options  • The admin should be able to filter bills by Billing Period (e.g., view only last 6 months or a specific year).  • There should be options to filter by Payment Status (Paid or Unpaid). 4. View and Export Bill Details  • Each entry in the bill history should include a "View Details" link that allows the admin to access a detailed view of the selected bill.  • The detailed view should include all bill information, including any applicable fees, taxes, and breakdowns if available.  • The page should provide an option to export the customer’s bill history to a PDF or CSV file.  • The exported file should contain all details displayed in the table, including the filtered results if filters are applied.

---

## US018: View Complaint- Admin
**Description:** As an admin I should be able to view complaint status for a customer.

**Acceptance Criteria:**
1. Search for Complaints  • The admin should be able to search for complaints using criteria such as:   • Customer ID or Consumer Number   • Complaint ID   • Complaint Type (e.g., billing issue, service interruption)   • Date Range for complaint submission  • The system should validate the Customer ID or Complaint ID to ensure they exist.  • If the system cannot find the entered Customer ID or Complaint ID, an error message should appear. 2. Display of Complaint List  • Upon entering search criteria, the system should display a list of matching complaints in a table format with the following columns:   • Complaint ID   • Customer ID/Consumer Number   • Complaint Type   • Date Submitted   • Status (e.g., Open, In Progress, Resolved)   • Last Updated Date  • Each complaint in the list should clearly show its current status, with visual indicators (e.g., color coding) to help admins quickly identify complaints that need attention. 3. Update Complaint Status  • The admin should be able to update the complaint status (e.g., from "Open" to "Resolved") and add notes if additional information is available.  • A confirmation message should appear upon successfully updating a complaint status.  • The page should have an option to export the complaint list to a CSV or PDF file.

---

## US019: Update Complaint- Admin
**Description:** As an admin I should be able to act on the complaint and assign the same to the SME.

**Acceptance Criteria:**
1. Search for Complaints  • The admin should be able to search for complaints using criteria such as:   • Customer ID or Consumer Number   • Complaint ID   • Complaint Type (e.g., billing issue, service interruption)   • Date Range for complaint submission  • The system should validate the Customer ID or Complaint ID to ensure they exist.  • If the system cannot find the entered Customer ID or Complaint ID, an error message should appear. 2. Display of Complaint Summary  • Upon entering search criteria, the system should display the details of the complaints with the following details:   • Complaint ID   • Customer ID/Consumer Number   • Complaint Type   • Date Submitted   • Status (e.g., Open, In Progress, Resolved)   • Last Updated Date  • Text area to enter the notes on status change.  3. Update Complaint Status  • The admin should be able to update the complaint status (e.g., from "Open" to "Resolved") and add notes if additional information is available.  • A confirmation message should appear upon successfully updating a complaint status.

---

## US020: View Complaint - SME
**Description:** As an SME I should be able to view complaint status and act on the same for a customer.

**Acceptance Criteria:**
1. Search for Complaints  • The SME should be able to search for complaints using criteria such as:   • Customer ID or Consumer Number   • Complaint ID   • Complaint Type (e.g., billing issue, service interruption)   • Date Range for complaint submission  • The system should validate the Customer ID or Complaint ID to ensure they exist.  • If the system cannot find the entered Customer ID or Complaint ID, an error message should appear. 2. Display of Complaint List  • Upon entering search criteria, the system should display a list of matching complaints in a table format with the following columns:   • Complaint ID   • Customer ID/Consumer Number   • Complaint Type   • Date Submitted   • Status (e.g., Open, In Progress, Resolved)   • Last Updated Date  • Each complaint in the list should clearly show its current status, with visual indicators (e.g., color coding) to help admins quickly identify complaints that need attention. 3. Update Complaint Status  • The SME should be able to update the complaint status (e.g., from "Inprogress" to "Resolved") and add notes if additional information is available.  • A confirmation message should appear upon successfully updating a complaint status.  • The page should have an option to export the complaint list to a CSV or PDF file.

---

