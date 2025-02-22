/*
 * Copyright (C) 2009 Swedish Institute of Computer Science (SICS) Copyright (C)
 * 2009 Royal Institute of Technology (KTH)
 *
 * KompicsToolbox is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package se.sics.ktoolbox.util.network.nat;

import com.google.common.base.Optional;
import java.util.Objects;
import se.sics.ktoolbox.util.network.KAddress;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class NatType {

  public final Nat.Type type;
  public final Nat.MappingPolicy mappingPolicy;
  public final Nat.AllocationPolicy allocationPolicy;
  public final int delta;
  public final Nat.FilteringPolicy filteringPolicy;
  public final long bindingTimeout;

  private NatType(Nat.Type type, Nat.MappingPolicy mappingPolicy, Nat.AllocationPolicy allocationPolicy, int delta,
    Nat.FilteringPolicy filteringPolicy, long bindingTimeout) {
    this.type = type;
    this.mappingPolicy = mappingPolicy;
    this.allocationPolicy = allocationPolicy;
    this.delta = delta;
    this.filteringPolicy = filteringPolicy;
    this.bindingTimeout = bindingTimeout;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 89 * hash + Objects.hashCode(this.type);
    hash = 89 * hash + Objects.hashCode(this.mappingPolicy);
    hash = 89 * hash + Objects.hashCode(this.allocationPolicy);
    hash = 89 * hash + this.delta;
    hash = 89 * hash + Objects.hashCode(this.filteringPolicy);
    hash = 89 * hash + (int) (this.bindingTimeout ^ (this.bindingTimeout >>> 32));
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final NatType other = (NatType) obj;
    if (this.type != other.type) {
      return false;
    }
    if (this.mappingPolicy != other.mappingPolicy) {
      return false;
    }
    if (this.allocationPolicy != other.allocationPolicy) {
      return false;
    }
    if (this.delta != other.delta) {
      return false;
    }
    if (this.filteringPolicy != other.filteringPolicy) {
      return false;
    }
    if (this.bindingTimeout != other.bindingTimeout) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    switch (type) {
      case OPEN:
      case FWL:
      case UDP_BLOCKED:
      case UPNP:
      case UNKNOWN:
      case PFW:
        return type.code;
      case NAT:
        return type.code + "_" + mappingPolicy.code + "_" + allocationPolicy.code + "_" + filteringPolicy.code;
      default:
        return "undefined";
    }
  }

  public static Optional<NatType> decode(String natType) {
    switch (natType) {
      case Nat.OPEN:
        return Optional.of(open());
      case Nat.FWL:
        return Optional.of(firewall());
      case Nat.X:
        return Optional.of(unknown());
      case Nat.UB:
        return Optional.of(udpBlocked());
      case Nat.UPNP:
        return Optional.of(upnp());
      case Nat.PFW:
        return Optional.of(natPortForwarding());
      default:
        if (natType.startsWith(Nat.NAT)) {
          String[] policies = natType.substring(Nat.NAT.length()).split("_");
          if (policies.length != 3) {
            return Optional.absent();
          }
          Nat.MappingPolicy mappingPolicy = Nat.MappingPolicy.decode(policies[0]);
          Nat.AllocationPolicy allocationPolicy = Nat.AllocationPolicy.decode(policies[1]);
          Nat.FilteringPolicy filteringPolicy = Nat.FilteringPolicy.decode(policies[2]);
          if (mappingPolicy == null || allocationPolicy == null || filteringPolicy == null) {
            return Optional.absent();
          }
          return Optional.of(nated(mappingPolicy, allocationPolicy, 0, filteringPolicy, 0));
        } else {
          return Optional.absent();
        }
    }
  }

  public boolean isSimpleNat() {
    return type.equals(Nat.Type.NAT)
      && allocationPolicy.equals(Nat.AllocationPolicy.PORT_PRESERVATION)
      && filteringPolicy.equals(Nat.FilteringPolicy.ENDPOINT_INDEPENDENT);
  }

  public boolean isOpen() {
    return type.equals(Nat.Type.OPEN);
  }

  public boolean isNatPortForwarding() {
    return type.equals(Nat.Type.PFW);
  }

  public boolean isBlocked() {
    return type.equals(Nat.Type.UDP_BLOCKED);
  }

  public boolean isNat() {
    return type.equals(Nat.Type.NAT);
  }
  public static NatType open() {
    return new NatType(Nat.Type.OPEN, null, null, 0, null, 0);
  }

  public static NatType natPortForwarding() {
    return new NatType(Nat.Type.PFW, null, null, 0, null, 0);
  }

  public static NatType firewall() {
    return new NatType(Nat.Type.FWL, null, null, 0, null, 0);
  }

  public static NatType udpBlocked() {
    return new NatType(Nat.Type.UDP_BLOCKED, null, null, 0, null, 0);
  }

  public static NatType upnp() {
    return new NatType(Nat.Type.UPNP, null, null, 0, null, 0);
  }

  public static NatType nated(Nat.MappingPolicy mappingPolicy, Nat.AllocationPolicy allocationPolicy, int delta,
    Nat.FilteringPolicy filteringPolicy, long bindingTimeout) {
    assert mappingPolicy != null;
    assert allocationPolicy != null;
    assert filteringPolicy != null;
    assert bindingTimeout > 0;
    return new NatType(Nat.Type.NAT, mappingPolicy, allocationPolicy, delta, filteringPolicy, bindingTimeout);
  }

  public static NatType unknown() {
    return new NatType(Nat.Type.UNKNOWN, null, null, 0, null, 0);
  }

  public static boolean isOpen(KAddress address) {
    if (address instanceof NatAwareAddress) {
      return Nat.Type.OPEN.equals(((NatAwareAddress) address).getNatType().type);
    } else {
      return true;
    }
  }

  public static boolean isNated(KAddress address) {
    if (address instanceof NatAwareAddress) {
      return Nat.Type.NAT.equals(((NatAwareAddress) address).getNatType().type);
    } else {
      return false;
    }
  }

  public static boolean isNatOpenPorts(KAddress address) {
    if (address instanceof NatAwareAddress) {
      return Nat.Type.PFW.equals(((NatAwareAddress) address).getNatType().type);
    } else {
      return false;
    }
  }

  public static boolean isUnknown(NatAwareAddress address) {
    if (address instanceof NatAwareAddress) {
      return Nat.Type.UNKNOWN.equals(((NatAwareAddress) address).getNatType().type);
    } else {
      return false;
    }
  }
}
