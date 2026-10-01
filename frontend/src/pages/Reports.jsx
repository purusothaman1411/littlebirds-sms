import { useState } from 'react';
import ClassResults from '../components/ClassResults.jsx';
import ReportCard from '../components/ReportCard.jsx';
import { usePermissions } from '../hooks/usePermissions.js';

export default function Reports() {
  const { has } = usePermissions();

  const tabs = [
    ...(has('REPORT_CARD_VIEW') ? [{ key: 'card', label: 'Report card' }] : []),
    ...(has('REPORT_CLASS_VIEW') ? [{ key: 'class', label: 'Class results' }] : []),
  ];
  const [tab, setTab] = useState(tabs[0]?.key);

  return (
    <div>
      <h1 className="no-print">Reports</h1>
      <div className="tabs no-print" role="tablist">
        {tabs.map((t) => (
          <button key={t.key} role="tab" aria-selected={tab === t.key}
            className={`tab ${tab === t.key ? 'active' : ''}`} onClick={() => setTab(t.key)}>
            {t.label}
          </button>
        ))}
      </div>
      {tab === 'card' && <ReportCard />}
      {tab === 'class' && <ClassResults />}
    </div>
  );
}
