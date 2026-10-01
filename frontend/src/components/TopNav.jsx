import { NavLink } from 'react-router-dom';
import { usePermissions } from '../hooks/usePermissions.js';
import { NAV_ITEMS } from './navItems.js';

/** Horizontal menu under the top bar. It scrolls sideways when there are more links than fit. */
export default function TopNav() {
  const { has, hasAny } = usePermissions();

  const visible = NAV_ITEMS.filter((item) => {
    if (item.permission) return has(item.permission);
    if (item.anyOf) return hasAny(item.anyOf);
    return true;
  });

  return (
    <nav className="topnav" aria-label="Main menu">
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
  );
}
