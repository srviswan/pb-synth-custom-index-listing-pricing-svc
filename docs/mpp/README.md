# MS Project Portfolio Assets

This folder contains ready-to-import CSVs and guidance for building a 16–18 project portfolio in Microsoft Project.

## Files
- `ResourcePool.csv` — roles/named resources with rates. Import and save as `ResourcePool.mpp`.
- `ProjectTemplate.csv` — starter WBS with custom fields and budget on Task 0.
- `projects/P01-Sample-Project.csv` — sample project (Strategic/MarketReach).
- `projects/P02-Sample-Project.csv` — sample project (Ops/Resiliency).
- `projects/P03-Sample-Project.csv` — sample project (Compliance).
- `mpp-setup.md` — full SOP for template, resource pool, master setup, and reporting.

## Import Steps (CSV -> MPP)
1) Open CSV in MS Project (File > Open > Browse, set filter to Text/CSV).
2) Use the Text Import Wizard:
   - Data type: Delimited, UTF-8.
   - Delimiters: Comma.
   - Map columns as provided (ID, Name, Duration, Predecessors, Outline Level, Text1_FundingBucket, Flag1_Approved, Text3_Stage, Text2_Risk, Number1_Priority, Text4_StrategicTheme, BudgetCost).
3) After import, **save as MPP**.
4) Enable Project Summary Task (Format > Project Summary Task) to see Budget Cost on Task 0.
5) Connect to the resource pool: Resources > Resource Pool > Share Resources > Use resources from `ResourcePool.mpp` (open both files).
6) For new projects, copy `ProjectTemplate.csv`, adjust BudgetCost/fields, import, connect to pool.

## Master Project Assembly
1) Create `PortfolioMaster.mpp`.
2) Insert Subproject for each converted MPP (Project > Subproject, keep links read/write).
3) Add columns: FundingBucket (Text1), Approved? (Flag1), Stage (Text3), StrategicTheme (Text4), Budget Cost, Cost, Cost Variance, Work, Work Variance, Finish, Finish Variance.
4) Group by FundingBucket > Stage; filter Approved? = Yes; highlight Cost > Budget Cost or Finish Variance > 0.

## Reporting Views
- Portfolio Summary: FundingBucket, Approved?, Stage, StrategicTheme, Budget vs Cost, Work vs Baseline, Finish Variance.
- At-Risk: Filter Cost > Budget OR Finish Variance > 0; columns add Risk and Owner.
- Resource Load: Use Resource Usage/Graph; group by Role (Text1_Role) if desired.
- Funding Bucket: Group by FundingBucket; show Budget, Cost, Variance, Work.

## Tips
- Set Status Date weekly; update Actual Work/Cost, % Complete.
- Baseline only after approvals or approved replans.
- Keep resource pool open read/write when updating assignments to avoid sync issues.

For full detail, see `docs/mpp/mpp-setup.md`.
