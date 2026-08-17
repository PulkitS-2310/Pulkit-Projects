# CPRE 2310 Final Project Report: The Hospital

> Sanitized GitHub-ready version. Exact IP addresses, passwords, and command strings have been replaced with placeholders such as `X.X.X.150`, `[REDACTED_COMMAND]`, and `[REDACTED_CREDENTIAL]`.

## Project Scope

This project follows the two-part hospital penetration testing assessment format from the instruction documents. The goal was to document host discovery, exploitation paths, sensitive information exposure, remediation steps, and indicators of compromise in a controlled lab environment.

---

# Final Project Part 1: The Hospital, Part 1

## Host discovery

| Host Machine | Open Ports / Services | Operating System |
|---|---|---|
| Ambulance Laptop (`X.X.X.158`) | `135/TCP` MSRPC, `139/TCP` NetBIOS-SSN, `445/TCP` Microsoft-DS / SMB, `3389/TCP` RDP | Microsoft Windows. The scan suggested possible versions such as Windows Server 2008 R2, Vista SP2, NetWare 6, or Windows 8/8.1 Update 1. |
| Database (`X.X.X.156`) | No open ports were identified in the scan result. | Ubuntu Linux 18.04 LTS, kernel version `4.15.0-213-generic`, x86_64 architecture. |
| Web Server (`X.X.X.150`) | `22/TCP` SSH, `44245/TCP` Telnet, `8000/TCP` HTTP | Linux-based system. The scan suggested Linux 4.15–5.19, OpenWrt 21.02, or MikroTik RouterOS as possible matches. |
| Clinician Desktop (`X.X.X.154`) | `135/TCP` MSRPC, `139/TCP` NetBIOS-SSN, `445/TCP` Microsoft service | Microsoft Windows. The scan suggested possible versions such as Windows XP SP2/SP3, Windows Embedded Standard 2009, or Windows Server 2003. |
| Reception Desktop (`X.X.X.152`) | `135/TCP` MSRPC, `139/TCP` NetBIOS-SSN, `445/TCP` Microsoft-DS / SMB, `3389/TCP` RDP, `49666/TCP`, `49667/TCP` | Microsoft Windows. The scan suggested possible versions such as Windows Server 2008 R2, Vista SP2, NetWare 6, or Windows 8/8.1 Update 1. |

## Exploiting three machines

| Host Machine | How did you gain access? | What specific harm could be done? | How can you remediate it? |
|---|---|---|---|
| Ambulance Laptop | I first compromised the Reception Desktop and then dumped NTLM password hashes from memory. I used one of those hashes for a user who had access to the Ambulance Laptop and performed a pass-the-hash style SMB authentication. This allowed authentication without knowing the plaintext password. | After gaining access, I was able to view sensitive paramedic incident reports stored on the machine. Those reports contained emergency call information, medical conditions, and details about people who contacted the hospital. An attacker could expose confidential patient details, use the information for blackmail, or pivot further into hospital systems. | The hospital should reduce administrative privileges on workstations so hashes cannot be easily dumped, disable weak LM/NTLM authentication where possible, enforce NTLMv2 or Kerberos, restrict SMB access, and encrypt sensitive incident reports. Privileged credentials should not be reused across systems. |
| Database | With physical access to the database system, I booted into a Kali live environment and mounted the database drive. After mounting the filesystem, I browsed the MySQL data directory and accessed database files directly. | I found a database file containing sensitive customer information such as usernames, SSNs, dates of birth, and credit card numbers. This could lead to identity theft, financial fraud, privacy violations, and direct harm to hospital patients or customers. | The hospital should enable full-disk encryption so the disk cannot be mounted without an encryption key. Sensitive database fields should also be encrypted at rest, physical access to servers should be restricted, and database files should never be readable outside proper database authentication controls. |
| Web Server | I attempted an SSH connection to the web server and found that authentication was misconfigured. After gaining access, I found a hidden `.hacked` directory containing a copy of `/etc/shadow`. I also found `secureCrypt.py` under the web application directory, which used weak reversible logic and a hard-coded key. | The exposed `/etc/shadow` file allows attackers to crack local account passwords offline without triggering login alerts. The weak custom encryption script could allow attackers to decrypt stored credentials or sensitive web application data. Together, these findings could let an attacker impersonate staff, steal records, escalate privileges, and maintain unauthorized access. | The hospital should reset all local, staff, and administrator passwords, rebuild the web server from a trusted clean image, remove malicious or unauthorized directories, replace weak custom encryption with trusted cryptographic libraries, harden file permissions, and enable logging, intrusion detection, and regular security monitoring. |

## Sensitive information

| Host Machine | What information I found, and why it is bad that I can see it |
|---|---|
| Ambulance Laptop | I was able to access paramedic incident reports. These reports included patient names, paramedic names, incident descriptions, dates, and emergency response details. This is confidential medical and operational information, and unauthorized access could violate patient privacy and expose people to blackmail, identity misuse, or targeted social engineering. |
| Database | I found `customers.ibd`, which contained sensitive customer records such as usernames, SSNs, dates of birth, and credit card numbers. This information could be used for identity theft and fraud, especially because the data was accessible after mounting the disk instead of requiring strong database-level authentication. |
| Web Server | I discovered a copy of `/etc/shadow` and the source code for a weak custom encryption script. The shadow file exposes password hashes for local users, and the encryption script shows how the web application protects sensitive data. Because the algorithm was reversible and the key was embedded in the code, an attacker could potentially decrypt stored credentials or other protected information. |

## Remediation

| Host Machine | Vulnerabilities, misconfigurations, sensitive information disclosures, or malpractices | Does the issue need to be fixed? Why or why not? | If actions were taken, how did you remediate it? If not, how would you remediate it? |
|---|---|---|---|
| Ambulance Laptop | The machine stored sensitive paramedic reports in an unencrypted or overly accessible way. The access path also showed poor credential protection and overly broad permissions, because reused hashes from another system allowed access through SMB. | Yes. This must be fixed because the machine contains sensitive patient and emergency response information. Unauthorized access could violate privacy, harm patients, and allow further movement inside the hospital network. | I would reduce administrative privileges on the Reception Desktop and other workstations, enforce stronger authentication, disable weak NTLM behavior where possible, restrict SMB access, and encrypt sensitive reports so they cannot be read without proper authorization. |
| Database | The database server disk was not encrypted, and raw database files could be accessed by mounting the drive. Sensitive information such as SSNs, card numbers, and dates of birth were stored in a way that could be read directly. | Yes. This must be fixed because anyone with physical or root-level access could steal, modify, or delete sensitive information without going through application or database authentication. | I did not modify the production-like system. The proper remediation would be enabling full-disk encryption, encrypting sensitive database fields, restricting physical access, using database access controls, and auditing for improper local file permissions. |
| Web Server | The web server contained a hidden directory with `/etc/shadow`, weak custom encryption code, hard-coded cryptographic material, and file permission issues. These findings indicate deep compromise and poor secure coding practices. | Yes. This must be fixed because password hashes and reversible encryption logic can allow attackers to steal credentials, decrypt sensitive data, and compromise connected systems. | The safest remediation is to rebuild the server from a clean trusted image, rotate all passwords, remove unauthorized files, replace custom encryption with trusted libraries, secure file permissions, review application code, and monitor for further signs of compromise. |

---

# Final Project Part 2: The Hospital, Part 2

## Exploiting three machines

| Host Machine | How did you gain access? | What specific harm could be done? | How can you remediate it? |
|---|---|---|---|
| Reception Desktop | I found that RDP was open on port `3389` during the previous scan. I used credentials discovered during Part 1 to authenticate to the Reception Desktop and was granted GUI access. The exact password and command are redacted for public upload. | With GUI access, an attacker could browse, copy, or delete HR documents, install malware, harvest credentials, use the desktop as a pivot point, and exfiltrate confidential employee information. | Enforce strong unique passwords, enable account lockout policies, restrict RDP to administrators or VPN-only access, enable Network Level Authentication, monitor login attempts with centralized logging or SIEM tools, and deploy endpoint protection. |
| Clinician Desktop | I found FTP enabled with anonymous login. Through FTP, I accessed sensitive system files such as `passwd` and `shadow`, then cracked weak passwords offline and used the recovered credentials to switch users and attempt privilege escalation. | An attacker could escalate to root-level access, modify or disable services, access sensitive data, create backdoors, add user accounts, or exfiltrate information. Credential reuse could also allow movement to other systems. | Disable anonymous FTP, replace FTP with SFTP/SCP, correct `/etc/passwd` and `/etc/shadow` permissions, rotate cracked or reused passwords, remove unused accounts, enforce strong password policies, and restrict sudo privileges. |
| Tom's Project | Anonymous FTP exposed sensitive files, including `passwd` and `shadow`. After cracking user credentials, I logged in as lower-privileged users and discovered that one user had broad sudo privileges. This allowed root-level access. | With root access, an attacker could deface or backdoor website files under `/var/www/html`, dump or modify the MySQL database, delete logs, hide persistence mechanisms, install rootkits, and compromise connected services. | Rotate all passwords, disable anonymous FTP, restrict sudo access to only essential personnel, audit web files and database credentials, remove unused legacy web files, harden Apache and MySQL, and monitor for privilege escalation or suspicious command execution. |

## Sensitive information

| Host Machine | What information I found, and why it is bad that I can see it |
|---|---|
| Reception Desktop | I found an HR records folder on the user's desktop. It contained `.contact` files with personal information such as names, email addresses, home and work addresses, job titles, dates of birth, anniversaries, and phone numbers. This data could support identity theft, phishing, social engineering, or employee impersonation. |
| Clinician Desktop | I accessed `/etc/passwd` and `/etc/shadow`, which exposed user lists, password hashes, and account metadata. After cracking the hashes, I recovered credentials for real users. This is critical because reused or privileged credentials can allow unauthorized login, impersonation, and lateral movement. |
| Tom's Project | I found web application files such as `login.php`, `connect.php`, and other frontend/backend files under `/var/www/html`. These files included hard-coded database credentials and legacy web code. I also found Apache configuration information. This is dangerous because source code and hard-coded credentials can expose database access, internal paths, and possible application vulnerabilities. |

## Remediation

| Host Machine | Vulnerabilities, misconfigurations, sensitive information disclosures, or malpractices | Does the issue need to be fixed? Why or why not? | If actions were taken, how did you remediate it? If not, how would you remediate it? |
|---|---|---|---|
| Reception Desktop | RDP was exposed, weak or reused credentials were present, MFA was not enforced, and HR data was stored in plaintext on the desktop without strong access control. | Yes. Open RDP combined with weak credentials is a common ransomware and intrusion path. The exposed HR records also create privacy and compliance risks. | RDP should be disabled or restricted to VPN/internal networks. The hospital should enforce MFA and strong unique passwords, move HR records to an access-controlled secure location, apply group policies to limit RDP access, and enable auditing of RDP sessions. |
| Clinician Desktop | Anonymous FTP was enabled, sensitive files were accessible over FTP, weak passwords were crackable, and unnecessary/default accounts appeared to exist. | Yes. Anonymous FTP access to sensitive system files is a severe misconfiguration that can lead directly to credential theft and system compromise. | Disable anonymous FTP in the FTP service configuration, correct permissions on sensitive files, rotate all cracked passwords, remove unused/default accounts, replace FTP with SFTP/SCP, and review sudo privileges. |
| Tom's Project | Anonymous FTP exposed password hashes, sudo privileges were overly broad, web files contained hard-coded database credentials, and legacy web files were present. | Yes. These issues allow root-level access, web application compromise, database compromise, and possible persistence. | Remove unnecessary sudo privileges, reset passwords, delete or archive legacy web files, remove hard-coded credentials, store secrets securely, disable anonymous FTP, harden Apache/MySQL, and perform a full application and log review. |

## Incident Response Table

| Host IP | What was accessed? | How was it accessed? | What was the impact of the incident? | How did you respond to the incident? | Screenshot reference |
|---|---|---|---|---|---|
| `X.X.X.150` | Website source code such as `index.php`, `login.php`, `connect.php`, configuration files with hard-coded database credentials, and legacy login-related files. | After gaining root access, I navigated to `/var/www/html` and inspected files using basic file viewing commands. Exact commands are redacted for public GitHub upload. | Exposed database credentials could allow access to medical records or backend systems. A compromised public web server could also be modified, backdoored, or used as a pivot point. | I documented the findings and file locations, recommended removing hard-coded credentials, suggested storing secrets in environment variables or a secure vault, and recommended a full security audit of the web application. | `P2-IR-01-www-source-code.png` |
| `X.X.X.158` | System account files such as `passwd` and `shadow`, plus cracked credentials for multiple users. | FTP allowed anonymous access to sensitive files. I downloaded the files and cracked hashes offline using a password-cracking tool. Exact commands and recovered credentials are redacted. | Credential reuse enabled lateral movement, privileged access, possible malware installation, and unauthorized access to sensitive information. | I recommended disabling anonymous FTP, rotating all affected passwords, restricting administrator access, using encrypted file transfer protocols, and reviewing logs for misuse. | `P2-IR-02-credential-files.png` |
| `X.X.X.175` | FTP files, user account files, Apache web content, application files, and possible MySQL credentials. | I used anonymous FTP to access files, cracked weak credentials, switched users, identified excessive sudo permissions, and gained root access. Exact commands are redacted. | The system was fully compromised. An attacker could read or edit web content, access database credentials, pivot to internal systems, and hide malicious activity. | I revoked unnecessary sudo privileges, recommended removing legacy users, rotating all passwords, reviewing logs, disabling anonymous FTP, and auditing the web/database stack. | `P2-IR-03-toms-project-root-access.png` |

---

# Final Notes for GitHub

This report is intentionally sanitized. The original course version should contain the numbered screenshots required by the instructions, but a public GitHub version should not include screenshots or text that reveal private IP addresses, passwords, hashes, patient/employee information, or school lab network details.
