# SonarCloud Setup Instructions

This document provides instructions for completing the SonarCloud setup for the Cinema Booking Application.

## Overview

The project has been configured with SonarCloud integration, but requires the following steps to be completed:

## Prerequisites

1. A SonarCloud account (free for open-source projects)
2. Administrative access to the GitHub repository

## Setup Steps

### 1. Create SonarCloud Project

1. Go to [SonarCloud](https://sonarcloud.io/)
2. Log in with your GitHub account
3. Click on the "+" icon in the top right and select "Analyze new project"
4. Select the `yuzhengchua/cinema-app` repository
5. Click "Set Up"

### 2. Configure Organization

- **Organization Key**: `yuzhengchua`
- **Project Key**: `yuzhengchua_cinema-app`

These values are already configured in:
- `pom.xml` (lines 32-34)
- `sonar-project.properties`

### 3. Add GitHub Secret

1. Go to your GitHub repository settings
2. Navigate to "Secrets and variables" → "Actions"
3. Click "New repository secret"
4. Add the following secret:
   - Name: `SONAR_TOKEN`
   - Value: Your SonarCloud token (found in SonarCloud under "My Account" → "Security")

### 4. Verify Integration

After the setup is complete:

1. Push a commit to trigger the CI workflow
2. The workflow will run SonarCloud analysis
3. Check the SonarCloud dashboard for results at: `https://sonarcloud.io/project/overview?id=yuzhengchua_cinema-app`

## Configuration Files

The following files have been added/modified for SonarCloud integration:

### Files Modified:
- `pom.xml` - Added SonarCloud Maven plugin and properties
- `.github/workflows/ci.yml` - Added SonarCloud scan step to CI workflow
- `README.md` - Added SonarCloud documentation

### Files Added:
- `sonar-project.properties` - SonarCloud project configuration

## Code Coverage

The project uses JaCoCo for code coverage:
- Coverage reports are generated in `target/site/jacoco/`
- SonarCloud automatically reads the JaCoCo XML report
- Coverage metrics will be displayed on the SonarCloud dashboard

## Running SonarCloud Analysis Locally

To run SonarCloud analysis on your local machine:

```bash
mvn clean verify sonar:sonar -Dsonar.token=YOUR_SONAR_TOKEN
```

Replace `YOUR_SONAR_TOKEN` with your actual SonarCloud token.

## Troubleshooting

### Issue: "Project not found" error
**Solution**: Ensure the project has been created on SonarCloud and the project key matches the configuration.

### Issue: "Authentication failed" error
**Solution**: Verify that the `SONAR_TOKEN` secret is correctly set in GitHub repository settings.

### Issue: No coverage data
**Solution**: Ensure tests are being run with `mvn clean verify` (not just `mvn test`).

## Additional Resources

- [SonarCloud Documentation](https://docs.sonarcloud.io/)
- [SonarCloud GitHub Integration](https://docs.sonarcloud.io/advanced-setup/ci-based-analysis/github-actions-for-sonarcloud/)
- [JaCoCo Maven Plugin](https://www.eclemma.org/jacoco/trunk/doc/maven.html)
