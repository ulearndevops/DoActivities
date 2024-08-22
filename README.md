Here’s an updated draft for your AWS support ticket:

---

**Subject:** Support Request: Assigning Static IPs or Defining Narrow IP Range for Replication, Conversion, Test, and Cutover Servers in AWS Application Migration Service

**Message:**

Hello AWS Support Team,

We are currently planning to migrate servers from a different data center to our AWS account using the AWS Application Migration Service (MGN). As part of this migration, we need to establish network connectivity between our on-premises data center and our AWS environment. However, our security team has requested a more narrowly defined CIDR range than what we currently have in our private subnets. The existing range spans 6656 IP addresses, but the security team wants to restrict access to only the actual servers involved in the migration.

We need guidance on the following:

1. **Static IP Assignment for Migration Servers:** Is there a way to assign static IP addresses for the replication, conversion, test, and cutover servers within our private CIDR ranges, instead of allowing them to dynamically pick an IP?

2. **Narrowing Down the IP Range for Security:** If assigning static IPs isn’t feasible, is there a method to define a smaller list of IPs (or a more limited CIDR range) that can be dedicated to the replication, conversion, test, and cutover servers? We would then share this list with our security team for allowlisting.

The goal is to balance our security requirements while ensuring the migration process remains smooth and uninterrupted.

Please let us know if there are alternative configurations or additional considerations we should be aware of.

Thank you for your support.

---

Feel free to adjust as needed!
