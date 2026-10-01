import { NavLink } from 'react-router-dom';
import { usePermissions } from '../hooks/usePermissions.js';
import { NAV_ITEMS } from './navItems.js';

export default function Sidebar() {
  const { has, hasAny } = usePermissions();

  const visible = NAV_ITEMS.filter((item) => {
    if (item.permission) return has(item.permission);
    if (item.anyOf) return hasAny(item.anyOf);
    return true;
  });

  return (
    <aside className="sidebar">
      <div className="brand">Little Birds</div>
      <nav>
        {visible.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.end}
            className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
          >
            {item.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
