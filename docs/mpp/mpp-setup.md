# Microsoft Project Portfolio Setup (16–18 Projects)

This guide provides a ready-to-use structure for managing 16–18 projects in Microsoft Project (MPP), including a standard single-project template, a shared resource pool, a master project configuration, and portfolio reporting layouts.

## 1) Standard Single-Project Template
Use this as the baseline for every project before inserting into the master plan.

### 1.1 Project Summary (Task 0)
- Enable **Project Summary Task** (Format > Project Summary Task).
- Set **Budget Cost** on Task 0 to represent the approved T-shirt estimate (top-down).
- Optional: Set **Baseline** after approval (Project > Set Baseline).

### 1.2 Custom Fields (recommended)
| Field | Type | Purpose / Example Values |
| --- | --- | --- |
| FundingBucket | Text1 | Ops, Growth, Compliance, Strategic |
| Approved? | Flag1 | Yes/No to mark approval |
| RiskLevel | Text2 | Low, Medium, High |
| Stage | Text3 | Initiation, Planning, Execution, Close |
| PriorityScore | Number1 | 1–100 or MoSCoW mapped to numbers |
| StrategicTheme | Text4 | e.g., CostOut, Resiliency, MarketReach |

### 1.3 Calendars & Options
- Use a **Standard (M–F, 8h)** calendar unless the project needs shift work.
- Set **Status Date** weekly for consistent variance tracking.

### 1.4 Task Structure (starter WBS)
- Initiation: Charter, Funding Approval (milestone)
- Planning: Schedule, Resource Plan, Risk Plan, Baseline (milestone)
- Execution: Build, Test, Deploy (milestones per release)
- Close: Handover, Lessons Learned (milestone)

### 1.5 Resources per Project
- Assign **Work resources** from the shared pool (see Section 2).
- Keep local resources to a minimum; prefer the pool for consistent rates.

## 2) Shared Resource Pool (Dedicated MPP)
Create a separate file (e.g., `ResourcePool.mpp`) and connect each project to it.

### 2.1 Define Resources
- Roles (optional) with standard rates: Dev, QA, BA, PM, Architect.
- Named resources (preferred if known): include Standard Rate, Max Units, Calendar.
- Add material or cost resources only if needed for non-labor items.

### 2.2 Connect Projects to the Pool
For each project MPP:
1. Open the project, then **Tools/Resource > Resource Pool > Share Resources**.
2. Select **Use resources > ResourcePool.mpp** (open in read/write when updating assignments).
3. Save; assignments now draw rates/availability from the pool.

## 3) Master Project Setup (Portfolio of 16–18 Projects)
Create a master file (e.g., `PortfolioMaster.mpp`) and insert each project as a subproject.

### 3.1 Insert Subprojects
- In the master, **Project > Subproject**, select each project file.
- Keep links **read/write** so updates flow both ways.

### 3.2 Recommended Columns (in Master View)
Add these columns to the Gantt/Task Sheet in the master:
- FundingBucket (Text1)
- Approved? (Flag1)
- Stage (Text3)
- StrategicTheme (Text4)
- Budget Cost
- Cost, Baseline Cost, Cost Variance
- Work, Baseline Work, Work Variance
- Finish, Baseline Finish, Finish Variance

### 3.3 Grouping & Sorting
Useful portfolio rollups:
- Group by **FundingBucket** then **Stage**.
- Filter **Approved? = Yes** for committed work.
- Custom Highlight: **Cost > Budget Cost** or **Finish Variance > 0** to flag at-risk items.

### 3.4 Cost & Work Rollups
- Ensure **Calculate project costs from resources** is enabled (default).
- Use Task 0 of each subproject for Budget Cost; actuals roll up via assigned resources.

## 4) Portfolio Reporting Layouts
These can be saved as views/tables/filters in the master file.

### 4.1 Portfolio Summary View (Table)
- Columns: Project, FundingBucket, Approved?, Stage, StrategicTheme, Budget Cost, Cost, Cost Variance, Work, Work Variance, Finish, Finish Variance.
- Group: FundingBucket > Stage.
- Filter: Approved? = Yes (toggle off for full view).

### 4.2 At-Risk Dashboard (Filter + Group)
- Filter: Cost > Budget Cost OR Finish Variance > 0.
- Group: Stage.
- Columns: Project, Cost, Budget Cost, Cost Variance, Finish, Finish Variance, RiskLevel, Owner.

### 4.3 Resource Load by Role
- In Resource Usage or Resource Graph, group by **RBS/Role** (use a custom Outline Code if available in the pool).
- Show Work vs. Capacity for the time-phased view; set timescale to weeks.

### 4.4 Funding Bucket View
- Group: FundingBucket.
- Columns: Budget Cost, Cost, Cost Variance, Work, Work Variance, Finish.
- Purpose: See spend and effort distribution per bucket.

## 5) SOP: Cadence & Controls
- Weekly: Update % Complete, Actual Work, Actual Cost; set Status Date; run At-Risk filter.
- Biweekly/Monthly: Refresh Baseline only when a replan is approved; export Portfolio Summary.
- Intake: For new projects, copy the single-project template, set Budget Cost on Task 0, populate custom fields, connect to the resource pool, then insert into the master.

## 6) Field Dictionary (Quick Reference)
- **Budget Cost** (Task 0): Approved top-down budget (T-shirt/blended).
- **FundingBucket (Text1)**: Category for funding source.
- **Approved? (Flag1)**: Approval state for execution.
- **RiskLevel (Text2)**: Qualitative risk indicator.
- **Stage (Text3)**: Lifecycle stage.
- **PriorityScore (Number1)**: Relative priority.
- **StrategicTheme (Text4)**: Strategic alignment tag.

## 7) Deliverables Checklist
- `ResourcePool.mpp` with roles/named resources and rates.
- `PortfolioMaster.mpp` with all 16–18 subprojects inserted.
- Single-project template file (or a saved MPP) with custom fields, starter WBS, and budget setup.
- Saved Views/Groups/Filters for: Portfolio Summary, At-Risk, Funding Bucket, Resource Load.

---
Use this guide to standardize intake, budgeting, resourcing, and reporting across all projects. EOF