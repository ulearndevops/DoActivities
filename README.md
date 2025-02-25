To use the same IAM role for ALB Ingress with another EKS cluster, you can follow these steps to associate the existing IAM role with the new cluster's ALB Ingress Controller using IAM Roles for Service Accounts (IRSA):

1. Ensure IAM Role Trust Policy Allows the New EKS Cluster

The IAM role must trust the OIDC provider of the new EKS cluster.

Get the OIDC provider URL for the new cluster:

aws eks describe-cluster --name <new-cluster-name> --query "cluster.identity.oidc.issuer" --output text

Check existing trust relationship of the IAM role:

aws iam get-role --role-name <existing-iam-role-name> --query 'Role.AssumeRolePolicyDocument'

If the new OIDC provider isn’t included, update the trust policy:

{
  "Effect": "Allow",
  "Principal": {
    "Federated": "arn:aws:iam::<account-id>:oidc-provider/<new-cluster-oidc-provider>"
  },
  "Action": "sts:AssumeRoleWithWebIdentity",
  "Condition": {
    "StringEquals": {
      "<new-cluster-oidc-provider>:sub": "system:serviceaccount:<namespace>:<service-account-name>"
    }
  }
}

Update the trust policy:

aws iam update-assume-role-policy --role-name <existing-iam-role-name> --policy-document file://trust-policy.json



---

2. Create the Service Account in the New Cluster

Create a Kubernetes service account for the ALB Ingress Controller in the new cluster, referencing the existing IAM role.

apiVersion: v1
kind: ServiceAccount
metadata:
  name: alb-ingress-controller
  namespace: kube-system
  annotations:
    eks.amazonaws.com/role-arn: arn:aws:iam::<account-id>:role/<existing-iam-role-name>

Apply it:

kubectl apply -f alb-ingress-serviceaccount.yaml


---

3. Deploy the ALB Ingress Controller

When deploying the ALB Ingress Controller in the new EKS cluster, specify the service account:

helm repo add eks https://aws.github.io/eks-charts
helm repo update

helm install alb-ingress-controller eks/aws-load-balancer-controller \
  -n kube-system \
  --set clusterName=<new-cluster-name> \
  --set serviceAccount.create=false \
  --set serviceAccount.name=alb-ingress-controller \
  --set region=<aws-region> \
  --set vpcId=<vpc-id>


---

4. Verify the ALB Ingress Controller

Check if the ALB Ingress Controller is using the correct IAM role:

kubectl -n kube-system get pods -l app.kubernetes.io/name=aws-load-balancer-controller
kubectl -n kube-system logs <alb-ingress-pod-name>

In the logs, verify the IAM role is attached and the controller is functioning correctly.


---

✅ Key Considerations:

The trust policy should allow both clusters’ OIDC providers if they differ.

Ensure the IAM role has all necessary policies (elasticloadbalancing:, ec2:, etc.) attached.

The namespace and service account name in the trust policy must match the new cluster.


This approach lets you reuse the same IAM role across multiple EKS clusters without creating new roles.

